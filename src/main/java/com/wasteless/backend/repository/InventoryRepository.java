package com.wasteless.backend.repository;

import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByUser(User user);

    // Find items expiring between two dates for a specific user
    @Query("SELECT i FROM InventoryItem i WHERE i.user = :user AND i.expiryDate BETWEEN :startDate AND :endDate ORDER BY i.expiryDate ASC")
    List<InventoryItem> findByUserAndExpiryDateBetween(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find items expiring on or before a specific date for a user
    @Query("SELECT i FROM InventoryItem i WHERE i.user = :user AND i.expiryDate <= :date ORDER BY i.expiryDate ASC")
    List<InventoryItem> findByUserAndExpiryDateBefore(@Param("user") User user, @Param("date") LocalDate date);

    // Find items by user and status
    List<InventoryItem> findByUserAndStatus(User user, InventoryItem.ItemStatus status);

    // Find consumed items within a date range
    @Query("SELECT i FROM InventoryItem i WHERE i.user = :user AND i.consumedAt IS NOT NULL AND i.consumedAt BETWEEN :startDate AND :endDate")
    List<InventoryItem> findConsumedItemsByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find consumed items by user, status, and date range
    @Query("SELECT i FROM InventoryItem i WHERE i.user = :user AND i.status = :status AND i.consumedAt IS NOT NULL AND i.consumedAt BETWEEN :startDate AND :endDate")
    List<InventoryItem> findByUserAndStatusAndConsumedAtBetween(@Param("user") User user, @Param("status") InventoryItem.ItemStatus status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
