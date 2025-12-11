package com.wasteless.backend.controller;

import com.wasteless.backend.dto.recipe.*;
import com.wasteless.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * POST /recipes/suggest?userId={userId}
     * Suggest recipes based on user's inventory
     * Prioritizes items expiring soon
     */
    @PostMapping("/suggest")
    public ResponseEntity<List<RecipeResponse>> suggestRecipes(
            @RequestParam Long userId,
            @RequestBody RecipeSuggestRequest request) {
        return ResponseEntity.ok(recipeService.suggestRecipes(userId, request));
    }

    /**
     * POST /recipes/search?userId={userId}
     * Search recipes by query, cuisine, diet, etc.
     */
    @PostMapping("/search")
    public ResponseEntity<List<RecipeResponse>> searchRecipes(
            @RequestParam Long userId,
            @RequestBody RecipeSearchRequest request) {
        return ResponseEntity.ok(recipeService.searchRecipes(userId, request));
    }

    /**
     * GET /recipes/{spoonacularId}?userId={userId}
     * Get detailed recipe information
     */
    @GetMapping("/{spoonacularId}")
    public ResponseEntity<RecipeResponse> getRecipeDetails(
            @PathVariable Long spoonacularId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(recipeService.getRecipeDetails(spoonacularId));
    }

    /**
     * POST /recipes/save?userId={userId}&spoonacularId={spoonacularId}
     * Save a recipe to user's collection
     */
    @PostMapping("/save")
    public ResponseEntity<RecipeResponse> saveRecipe(
            @RequestParam Long userId,
            @RequestParam Long spoonacularId) {
        return ResponseEntity.ok(recipeService.saveRecipe(userId, spoonacularId));
    }

    /**
     * GET /recipes/saved?userId={userId}
     * Get all saved recipes for a user
     */
    @GetMapping("/saved")
    public ResponseEntity<List<RecipeResponse>> getSavedRecipes(@RequestParam Long userId) {
        return ResponseEntity.ok(recipeService.getSavedRecipes(userId));
    }

    /**
     * GET /recipes/cooked?userId={userId}
     * Get all cooked recipes for a user
     */
    @GetMapping("/cooked")
    public ResponseEntity<List<RecipeResponse>> getCookedRecipes(@RequestParam Long userId) {
        return ResponseEntity.ok(recipeService.getCookedRecipes(userId));
    }

    /**
     * POST /recipes/mark-as-cooked?userId={userId}
     * Mark a recipe as cooked and optionally deduct ingredients from inventory
     */
    @PostMapping("/mark-as-cooked")
    public ResponseEntity<RecipeResponse> markAsCooked(
            @RequestParam Long userId,
            @RequestBody MarkAsCookedRequest request) {
        return ResponseEntity.ok(recipeService.markAsCooked(userId, request));
    }

    /**
     * DELETE /recipes/{recipeId}?userId={userId}
     * Delete a saved recipe
     */
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long recipeId,
            @RequestParam Long userId) {
        recipeService.deleteRecipe(userId, recipeId);
        return ResponseEntity.noContent().build();
    }
}
