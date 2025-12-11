package com.wasteless.backend.dto.impact;

import com.wasteless.backend.model.InventoryItem;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemStatusRequest {

    @NotNull(message = "Status is required")
    private InventoryItem.ItemStatus status; // EATEN or WASTED

    private Double estimatedValue; // Optional: user can input estimated value in Naira
}
