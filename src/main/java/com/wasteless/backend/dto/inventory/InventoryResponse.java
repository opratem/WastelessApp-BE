package com.wasteless.backend.dto.inventory;

import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private Long id;
    private String name;
    private int quantity;
    private String unit;
    private String category;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;
    private String storageLocation;
    private Double estimatedValue;
    private long daysUntilExpiry;

    // Package information
    private Integer packageSize;
    private String packageUnit;

    // Spoilage tracking
    private Boolean spoilsAfterOpening;
    private Boolean isOpened;
    private LocalDate dateOpened;
    private Integer daysUntilSpoilage;

    // Image
    private String imageUrl;

    // Helper method to compute days left
    public static InventoryResponse fromEntity(com.wasteless.backend.model.InventoryItem item) {
        long daysLeft = 0;
        if (item.getExpiryDate() != null) {
            daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), item.getExpiryDate());
        }

        return InventoryResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .unit(item.getUnit())
                .category(item.getCategory())
                .purchaseDate(item.getPurchaseDate())
                .expiryDate(item.getExpiryDate())
                .storageLocation(item.getStorageLocation())
                .estimatedValue(item.getEstimatedValue())
                .daysUntilExpiry(daysLeft)
                .packageSize(item.getPackageSize())
                .packageUnit(item.getPackageUnit())
                .spoilsAfterOpening(item.getSpoilsAfterOpening())
                .isOpened(item.getIsOpened())
                .dateOpened(item.getDateOpened())
                .daysUntilSpoilage(item.getDaysUntilSpoilage())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
