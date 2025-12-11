package com.wasteless.backend.dto.recipe;

import com.wasteless.backend.model.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeResponse {
    private Long id;
    private Long spoonacularId;
    private String title;
    private String image;
    private Integer readyInMinutes;
    private Integer servings;
    private String summary;
    private String instructions;
    private List<String> ingredients;
    private List<String> usedIngredients;
    private List<String> missedIngredients;
    private Integer usedIngredientCount;
    private Integer missedIngredientCount;
    private Boolean isCooked;
    private LocalDateTime cookedAt;
    private LocalDateTime savedAt;

    public static RecipeResponse fromEntity(Recipe recipe) {
        return RecipeResponse.builder()
                .id(recipe.getId())
                .spoonacularId(recipe.getSpoonacularId())
                .title(recipe.getTitle())
                .image(recipe.getImage())
                .readyInMinutes(recipe.getReadyInMinutes())
                .servings(recipe.getServings())
                .summary(recipe.getSummary())
                .instructions(recipe.getInstructions())
                .ingredients(recipe.getIngredients())
                .usedIngredients(recipe.getUsedIngredients())
                .missedIngredients(recipe.getMissedIngredients())
                .usedIngredientCount(recipe.getUsedIngredientCount())
                .missedIngredientCount(recipe.getMissedIngredientCount())
                .isCooked(recipe.getIsCooked())
                .cookedAt(recipe.getCookedAt())
                .savedAt(recipe.getSavedAt())
                .build();
    }
}
