package com.wasteless.backend.controller;

import com.wasteless.backend.dto.inventory.InventoryRequest;
import com.wasteless.backend.dto.inventory.InventoryResponse;
import com.wasteless.backend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{userId}/all")
    public ResponseEntity<List<InventoryResponse>> getAllItems(@PathVariable Long userId) {
        return ResponseEntity.ok(inventoryService.getAllItems(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<InventoryResponse> addItem(@PathVariable Long userId, @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.addItem(userId, request));
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<InventoryResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getItemById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<InventoryResponse> updateItem(@PathVariable Long id, @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateItem(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
