package com.wasteless.backend.service;

import com.wasteless.backend.dto.inventory.InventoryRequest;
import com.wasteless.backend.dto.inventory.InventoryResponse;
import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.InventoryRepository;
import com.wasteless.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public List<InventoryResponse> getAllItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return inventoryRepository.findByUser(user)
                .stream()
                .map(InventoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public InventoryResponse addItem(Long userId, InventoryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        InventoryItem item = InventoryItem.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .category(request.getCategory())
                .purchaseDate(request.getPurchaseDate())
                .expiryDate(request.getExpiryDate())
                .storageLocation(request.getStorageLocation())
                .estimatedValue(request.getEstimatedValue())
                .packageSize(request.getPackageSize())
                .packageUnit(request.getPackageUnit())
                .spoilsAfterOpening(request.getSpoilsAfterOpening())
                .isOpened(request.getIsOpened())
                .dateOpened(request.getDateOpened())
                .daysUntilSpoilage(request.getDaysUntilSpoilage())
                .imageUrl(request.getImageUrl())
                .user(user)
                .build();

        return InventoryResponse.fromEntity(inventoryRepository.save(item));
    }

    public InventoryResponse getItemById(Long id) {
        InventoryItem item = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return InventoryResponse.fromEntity(item);
    }

    public InventoryResponse updateItem(Long id, InventoryRequest request) {
        InventoryItem existing = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        existing.setName(request.getName());
        existing.setQuantity(request.getQuantity());
        existing.setUnit(request.getUnit());
        existing.setCategory(request.getCategory());
        existing.setPurchaseDate(request.getPurchaseDate());
        existing.setExpiryDate(request.getExpiryDate());
        existing.setStorageLocation(request.getStorageLocation());
        existing.setEstimatedValue(request.getEstimatedValue());
        existing.setPackageSize(request.getPackageSize());
        existing.setPackageUnit(request.getPackageUnit());
        existing.setSpoilsAfterOpening(request.getSpoilsAfterOpening());
        existing.setIsOpened(request.getIsOpened());
        existing.setDateOpened(request.getDateOpened());
        existing.setDaysUntilSpoilage(request.getDaysUntilSpoilage());

        // Only update image if a new one is provided
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            existing.setImageUrl(request.getImageUrl());
        }

        return InventoryResponse.fromEntity(inventoryRepository.save(existing));
    }

    public void deleteItem(Long id) {
        inventoryRepository.deleteById(id);
    }
}
