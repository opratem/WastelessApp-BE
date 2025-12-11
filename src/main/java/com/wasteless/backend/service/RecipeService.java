package com.wasteless.backend.service;

import com.wasteless.backend.config.SpoonacularConfig;
import com.wasteless.backend.dto.recipe.*;
import com.wasteless.backend.dto.recipe.spoonacular.SpoonacularRecipe;
import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.model.Recipe;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.InventoryRepository;
import com.wasteless.backend.repository.RecipeRepository;
import com.wasteless.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final WebClient spoonacularWebClient;
    private final SpoonacularConfig spoonacularConfig;
    private final RecipeRepository recipeRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    /**
     * Search recipes by query using Spoonacular API
     */
    public List<RecipeResponse> searchRecipes(Long userId, RecipeSearchRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int numberOfRecipes = request.getNumberOfRecipes() != null ? request.getNumberOfRecipes() : 10;

        try {
            SpoonacularRecipe[] recipes = spoonacularWebClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/recipes/complexSearch")
                                .queryParam("apiKey", spoonacularConfig.getApiKey())
                                .queryParam("number", numberOfRecipes)
                                .queryParam("addRecipeInformation", true)
                                .queryParam("fillIngredients", true);

                        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
                            uriBuilder.queryParam("query", request.getQuery());
                        }
                        if (request.getCuisine() != null && !request.getCuisine().isEmpty()) {
                            uriBuilder.queryParam("cuisine", request.getCuisine());
                        }
                        if (request.getDiet() != null && !request.getDiet().isEmpty()) {
                            uriBuilder.queryParam("diet", request.getDiet());
                        }
                        if (request.getMaxReadyTime() != null) {
                            uriBuilder.queryParam("maxReadyTime", request.getMaxReadyTime());
                        }

                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(SpoonacularSearchResponse.class)
                    .map(response -> response.getResults().toArray(new SpoonacularRecipe[0]))
                    .block();

            return recipes != null ?
                    List.of(recipes).stream()
                            .map(this::convertSpoonacularToResponse)
                            .collect(Collectors.toList())
                    : new ArrayList<>();

        } catch (Exception e) {
            throw new RuntimeException("Error searching recipes: " + e.getMessage(), e);
        }
    }

    /**
     * Suggest recipes based on user's inventory items
     * Prioritizes items expiring soon
     */
    public List<RecipeResponse> suggestRecipes(Long userId, RecipeSuggestRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get inventory items
        List<InventoryItem> items;
        if (request.getInventoryItemIds() != null && !request.getInventoryItemIds().isEmpty()) {
            items = request.getInventoryItemIds().stream()
                    .map(id -> inventoryRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Inventory item not found: " + id)))
                    .collect(Collectors.toList());
        } else {
            items = inventoryRepository.findByUser(user);
        }

        if (items.isEmpty()) {
            throw new RuntimeException("No inventory items found");
        }

        // Prioritize expiring items if requested
        boolean prioritizeExpiring = request.getPrioritizeExpiring() != null ?
                request.getPrioritizeExpiring() : true;

        if (prioritizeExpiring) {
            items = items.stream()
                    .sorted(Comparator.comparing(InventoryItem::getExpiryDate,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }

        // Build ingredient list
        String ingredients = items.stream()
                .map(InventoryItem::getName)
                .collect(Collectors.joining(","));

        int numberOfRecipes = request.getNumberOfRecipes() != null ? request.getNumberOfRecipes() : 5;
        int maxMissing = request.getMaxMissingIngredients() != null ? request.getMaxMissingIngredients() : 2;

        try {
            SpoonacularRecipe[] recipes = spoonacularWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/recipes/findByIngredients")
                            .queryParam("apiKey", spoonacularConfig.getApiKey())
                            .queryParam("ingredients", ingredients)
                            .queryParam("number", numberOfRecipes)
                            .queryParam("ranking", prioritizeExpiring ? 1 : 2)
                            .queryParam("ignorePantry", true)
                            .build())
                    .retrieve()
                    .bodyToMono(SpoonacularRecipe[].class)
                    .block();

            if (recipes == null || recipes.length == 0) {
                return new ArrayList<>();
            }

            // Filter by max missing ingredients and get detailed info
            List<RecipeResponse> result = new ArrayList<>();
            for (SpoonacularRecipe recipe : recipes) {
                if (recipe.getMissedIngredientCount() <= maxMissing) {
                    RecipeResponse detailed = getRecipeDetails(recipe.getId());
                    if (detailed != null) {
                        detailed.setUsedIngredients(
                                recipe.getUsedIngredients().stream()
                                        .map(SpoonacularRecipe.UsedIngredient::getOriginal)
                                        .collect(Collectors.toList())
                        );
                        detailed.setMissedIngredients(
                                recipe.getMissedIngredients().stream()
                                        .map(SpoonacularRecipe.MissedIngredient::getOriginal)
                                        .collect(Collectors.toList())
                        );
                        detailed.setUsedIngredientCount(recipe.getUsedIngredientCount());
                        detailed.setMissedIngredientCount(recipe.getMissedIngredientCount());
                        result.add(detailed);
                    }
                }
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error suggesting recipes: " + e.getMessage(), e);
        }
    }

    /**
     * Get detailed recipe information from Spoonacular
     */
    public RecipeResponse getRecipeDetails(Long spoonacularId) {
        try {
            SpoonacularRecipe recipe = spoonacularWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/recipes/{id}/information")
                            .queryParam("apiKey", spoonacularConfig.getApiKey())
                            .build(spoonacularId))
                    .retrieve()
                    .bodyToMono(SpoonacularRecipe.class)
                    .block();

            return recipe != null ? convertSpoonacularToResponse(recipe) : null;

        } catch (Exception e) {
            throw new RuntimeException("Error getting recipe details: " + e.getMessage(), e);
        }
    }

    /**
     * Save a recipe to user's collection
     */
    @Transactional
    public RecipeResponse saveRecipe(Long userId, Long spoonacularId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (recipeRepository.existsByUserAndSpoonacularId(user, spoonacularId)) {
            throw new RuntimeException("Recipe already saved");
        }

        RecipeResponse recipeDetails = getRecipeDetails(spoonacularId);
        if (recipeDetails == null) {
            throw new RuntimeException("Recipe not found");
        }

        Recipe recipe = Recipe.builder()
                .spoonacularId(spoonacularId)
                .title(recipeDetails.getTitle())
                .image(recipeDetails.getImage())
                .readyInMinutes(recipeDetails.getReadyInMinutes())
                .servings(recipeDetails.getServings())
                .summary(recipeDetails.getSummary())
                .instructions(recipeDetails.getInstructions())
                .ingredients(recipeDetails.getIngredients())
                .usedIngredients(recipeDetails.getUsedIngredients() != null ?
                        recipeDetails.getUsedIngredients() : new ArrayList<>())
                .missedIngredients(recipeDetails.getMissedIngredients() != null ?
                        recipeDetails.getMissedIngredients() : new ArrayList<>())
                .usedIngredientCount(recipeDetails.getUsedIngredientCount() != null ?
                        recipeDetails.getUsedIngredientCount() : 0)
                .missedIngredientCount(recipeDetails.getMissedIngredientCount() != null ?
                        recipeDetails.getMissedIngredientCount() : 0)
                .user(user)
                .savedAt(LocalDateTime.now())
                .isCooked(false)
                .build();

        Recipe saved = recipeRepository.save(recipe);
        return RecipeResponse.fromEntity(saved);
    }

    /**
     * Get all saved recipes for a user
     */
    public List<RecipeResponse> getSavedRecipes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recipeRepository.findByUser(user).stream()
                .map(RecipeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get cooked recipes for a user
     */
    public List<RecipeResponse> getCookedRecipes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return recipeRepository.findByUserAndIsCooked(user, true).stream()
                .map(RecipeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Mark a recipe as cooked and optionally deduct ingredients from inventory
     */
    @Transactional
    public RecipeResponse markAsCooked(Long userId, MarkAsCookedRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (!recipe.getUser().getId().equals(userId)) {
            throw new RuntimeException("Recipe does not belong to user");
        }

        recipe.setIsCooked(true);
        recipe.setCookedAt(LocalDateTime.now());

        boolean deduct = request.getDeductFromInventory() != null ?
                request.getDeductFromInventory() : true;

        if (deduct && request.getIngredientsUsedIds() != null && !request.getIngredientsUsedIds().isEmpty()) {
            for (Long itemId : request.getIngredientsUsedIds()) {
                InventoryItem item = inventoryRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Inventory item not found: " + itemId));

                if (!item.getUser().getId().equals(userId)) {
                    throw new RuntimeException("Inventory item does not belong to user");
                }

                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    inventoryRepository.save(item);
                } else {
                    inventoryRepository.delete(item);
                }
            }
        }

        Recipe saved = recipeRepository.save(recipe);
        return RecipeResponse.fromEntity(saved);
    }

    /**
     * Delete a saved recipe
     */
    @Transactional
    public void deleteRecipe(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (!recipe.getUser().getId().equals(userId)) {
            throw new RuntimeException("Recipe does not belong to user");
        }

        recipeRepository.delete(recipe);
    }

    private RecipeResponse convertSpoonacularToResponse(SpoonacularRecipe spoonacular) {
        List<String> ingredients = new ArrayList<>();
        if (spoonacular.getExtendedIngredients() != null) {
            ingredients = spoonacular.getExtendedIngredients().stream()
                    .map(SpoonacularRecipe.ExtendedIngredient::getOriginal)
                    .collect(Collectors.toList());
        }

        return RecipeResponse.builder()
                .spoonacularId(spoonacular.getId())
                .title(spoonacular.getTitle())
                .image(spoonacular.getImage())
                .readyInMinutes(spoonacular.getReadyInMinutes())
                .servings(spoonacular.getServings())
                .summary(spoonacular.getSummary())
                .instructions(spoonacular.getInstructions())
                .ingredients(ingredients)
                .build();
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class SpoonacularSearchResponse {
        private List<SpoonacularRecipe> results;
        private int offset;
        private int number;
        private int totalResults;
    }
}
