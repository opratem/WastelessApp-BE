package com.wasteless.backend.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSuggestRequest {
    private List<Long> inventoryItemIds; // IDs of items from user's inventory
    private Integer numberOfRecipes; // Number of recipe suggestions (default: 5)
    private Boolean prioritizeExpiring; // Prioritize items expiring soon (default: true)
    private Integer maxMissingIngredients; // Max missing ingredients allowed (default: 2)
}
