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
public class MarkAsCookedRequest {
    private Long recipeId;
    private List<Long> ingredientsUsedIds; // IDs of inventory items used in cooking
    private Boolean deductFromInventory; // Whether to reduce/delete items from inventory (default: true)
}
