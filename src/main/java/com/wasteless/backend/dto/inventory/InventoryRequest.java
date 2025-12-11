package com.wasteless.backend.dto.inventory;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {
    private String name;
    private int quantity;
    private String unit;
    private String category;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String storageLocation;
    private Double estimatedValue;

    // Package information
    private Integer packageSize;
    private String packageUnit;

    // Spoilage tracking
    private Boolean spoilsAfterOpening;
    private Boolean isOpened;
    private LocalDate dateOpened;
    private Integer daysUntilSpoilage;

    // Image (Base64 encoded or URL)
    private String imageUrl;
}
