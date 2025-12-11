package com.wasteless.backend.dto.recipe.spoonacular;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularRecipe {
    private Long id;
    private String title;
    private String image;
    private Integer readyInMinutes;
    private Integer servings;
    private String summary;
    private String instructions;
    private List<ExtendedIngredient> extendedIngredients;
    private List<UsedIngredient> usedIngredients;
    private List<MissedIngredient> missedIngredients;
    private Integer usedIngredientCount;
    private Integer missedIngredientCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExtendedIngredient {
        private Long id;
        private String name;
        private String original;
        private Double amount;
        private String unit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsedIngredient {
        private Long id;
        private String name;
        private String original;
        private Double amount;
        private String unit;
        private String image;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MissedIngredient {
        private Long id;
        private String name;
        private String original;
        private Double amount;
        private String unit;
        private String image;
    }
}
