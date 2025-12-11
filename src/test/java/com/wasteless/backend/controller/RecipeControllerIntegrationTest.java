package com.wasteless.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasteless.backend.dto.recipe.MarkAsCookedRequest;
import com.wasteless.backend.dto.recipe.RecipeSearchRequest;
import com.wasteless.backend.dto.recipe.RecipeSuggestRequest;
import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.model.Recipe;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.InventoryRepository;
import com.wasteless.backend.repository.RecipeRepository;
import com.wasteless.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RecipeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private InventoryItem testItem1;
    private InventoryItem testItem2;

    @BeforeEach
    public void setUp() {
        // Create test user
        testUser = User.builder()
                .fullName("recipe test")
                .email("recipetest@test.com")
                .password(passwordEncoder.encode("password123"))
                .build();
        testUser = userRepository.save(testUser);

        // Create test inventory items
        testItem1 = InventoryItem.builder()
                .name("Chicken")
                .quantity(2)
                .category("Meat")
                .storageLocation("Fridge")
                .purchaseDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(2))
                .user(testUser)
                .build();
        testItem1 = inventoryRepository.save(testItem1);

        testItem2 = InventoryItem.builder()
                .name("Tomatoes")
                .quantity(5)
                .category("Vegetables")
                .storageLocation("Fridge")
                .purchaseDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(4))
                .user(testUser)
                .build();
        testItem2 = inventoryRepository.save(testItem2);
    }

    @Test
    public void testSuggestRecipes() throws Exception {
        RecipeSuggestRequest request = RecipeSuggestRequest.builder()
                .inventoryItemIds(Arrays.asList(testItem1.getId(), testItem2.getId()))
                .numberOfRecipes(5)
                .prioritizeExpiring(true)
                .maxMissingIngredients(2)
                .build();

        // Note: This test will fail without a valid Spoonacular API key
        // For now, we're just testing the endpoint structure
        mockMvc.perform(post("/api/v1/recipes/suggest")
                        .param("userId", testUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchRecipes() throws Exception {
        RecipeSearchRequest request = RecipeSearchRequest.builder()
                .query("pasta")
                .cuisine("italian")
                .numberOfRecipes(10)
                .build();

        // Note: This test will fail without a valid Spoonacular API key
        mockMvc.perform(post("/api/v1/recipes/search")
                        .param("userId", testUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSaveRecipe() throws Exception {
        Long spoonacularId = 716429L; // Sample Spoonacular recipe ID

        // Note: This test will fail without a valid Spoonacular API key
        mockMvc.perform(post("/api/v1/recipes/save")
                        .param("userId", testUser.getId().toString())
                        .param("spoonacularId", spoonacularId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSavedRecipes() throws Exception {
        // Create a saved recipe
        Recipe savedRecipe = Recipe.builder()
                .spoonacularId(12345L)
                .title("Test Recipe")
                .image("http://example.com/image.jpg")
                .readyInMinutes(30)
                .servings(4)
                .summary("A test recipe")
                .instructions("Mix and cook")
                .user(testUser)
                .savedAt(LocalDateTime.now())
                .isCooked(false)
                .build();
        recipeRepository.save(savedRecipe);

        mockMvc.perform(get("/api/v1/recipes/saved")
                        .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Recipe"));
    }

    @Test
    public void testGetCookedRecipes() throws Exception {
        // Create a cooked recipe
        Recipe cookedRecipe = Recipe.builder()
                .spoonacularId(54321L)
                .title("Cooked Test Recipe")
                .image("http://example.com/cooked.jpg")
                .readyInMinutes(45)
                .servings(2)
                .summary("A cooked test recipe")
                .instructions("Cook thoroughly")
                .user(testUser)
                .savedAt(LocalDateTime.now().minusDays(1))
                .cookedAt(LocalDateTime.now())
                .isCooked(true)
                .build();
        recipeRepository.save(cookedRecipe);

        mockMvc.perform(get("/api/v1/recipes/cooked")
                        .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Cooked Test Recipe"))
                .andExpect(jsonPath("$[0].isCooked").value(true));
    }

    @Test
    public void testMarkAsCooked() throws Exception {
        // Create a saved recipe
        Recipe recipe = Recipe.builder()
                .spoonacularId(99999L)
                .title("Recipe to Cook")
                .image("http://example.com/tocook.jpg")
                .readyInMinutes(20)
                .servings(3)
                .summary("Recipe for cooking test")
                .instructions("Cook it")
                .user(testUser)
                .savedAt(LocalDateTime.now())
                .isCooked(false)
                .build();
        recipe = recipeRepository.save(recipe);

        MarkAsCookedRequest request = MarkAsCookedRequest.builder()
                .recipeId(recipe.getId())
                .ingredientsUsedIds(Arrays.asList(testItem1.getId()))
                .deductFromInventory(true)
                .build();

        mockMvc.perform(post("/api/v1/recipes/mark-as-cooked")
                        .param("userId", testUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCooked").value(true));
    }

    @Test
    public void testDeleteRecipe() throws Exception {
        // Create a recipe to delete
        Recipe recipe = Recipe.builder()
                .spoonacularId(77777L)
                .title("Recipe to Delete")
                .image("http://example.com/delete.jpg")
                .readyInMinutes(15)
                .servings(1)
                .summary("Will be deleted")
                .instructions("Delete me")
                .user(testUser)
                .savedAt(LocalDateTime.now())
                .isCooked(false)
                .build();
        recipe = recipeRepository.save(recipe);

        mockMvc.perform(delete("/api/v1/recipes/" + recipe.getId())
                        .param("userId", testUser.getId().toString()))
                .andExpect(status().isNoContent());
    }
}
