package com.wasteless.backend.dto.expiration;

import com.wasteless.backend.model.InventoryItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpcomingExpirationResponse {
    private Long itemId;
    private String name;
    private String category;
    private Integer quantity;
    private LocalDate expiryDate;
    private String storageLocation;
    private Long daysUntilExpiry;
    private String urgencyLevel; // "CRITICAL" (0-1 days), "HIGH" (2-3 days), "MEDIUM" (4-7 days)

    public static UpcomingExpirationResponse fromEntity(InventoryItem item) {
        LocalDate today = LocalDate.now();
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, item.getExpiryDate());

        String urgencyLevel;
        if (daysUntilExpiry <= 1) {
            urgencyLevel = "CRITICAL";
        } else if (daysUntilExpiry <= 3) {
            urgencyLevel = "HIGH";
        } else if (daysUntilExpiry <= 7) {
            urgencyLevel = "MEDIUM";
        }else {
            urgencyLevel = "LOW";
        }

        return UpcomingExpirationResponse.builder()
                .itemId(item.getId())
                .name(item.getName())
                .category(item.getCategory())
                .quantity(item.getQuantity())
                .expiryDate(item.getExpiryDate())
                .storageLocation(item.getStorageLocation())
                .daysUntilExpiry(daysUntilExpiry)
                .urgencyLevel(urgencyLevel)
                .build();
    }
}
