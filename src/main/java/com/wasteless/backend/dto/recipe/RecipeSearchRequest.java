package com.wasteless.backend.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSearchRequest {
    private String query; // Search term (e.g., "pasta", "chicken soup")
    private String cuisine; // Cuisine type (e.g., "italian", "mexican")
    private String diet; // Diet type (e.g., "vegetarian", "vegan", "gluten-free")
    private Integer maxReadyTime; // Maximum cooking time in minutes
    private Integer numberOfRecipes; // Number of results (default: 10)
}
