package com.wasteless.backend.controller;

import com.wasteless.backend.dto.expiration.ExpirationSettingsRequest;
import com.wasteless.backend.dto.expiration.ExpirationSettingsResponse;
import com.wasteless.backend.dto.expiration.UpcomingExpirationResponse;
import com.wasteless.backend.service.ExpirationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class ExpirationController {

    private final ExpirationService expirationService;

    /**
     * GET /alerts/upcoming?userId={userId}&days={days}
     * Get items expiring within the next N days (default 7 days)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<UpcomingExpirationResponse>> getUpcomingExpirations(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        return ResponseEntity.ok(expirationService.getUpcomingExpirations(userId, days));
    }

    /**
     * GET /alerts/today?userId={userId}
     * Get items expiring today
     */
    @GetMapping("/today")
    public ResponseEntity<List<UpcomingExpirationResponse>> getExpirationsToday(@RequestParam Long userId) {
        return ResponseEntity.ok(expirationService.getExpirationsToday(userId));
    }

    /**
     * GET /alerts/critical?userId={userId}
     * Get critical items (expiring in 0-1 days)
     */
    @GetMapping("/critical")
    public ResponseEntity<List<UpcomingExpirationResponse>> getCriticalExpirations(@RequestParam Long userId) {
        return ResponseEntity.ok(expirationService.getCriticalExpirations(userId));
    }

    /**
     * GET /alerts/settings?userId={userId}
     * Get alert settings for a user
     */
    @GetMapping("/settings")
    public ResponseEntity<ExpirationSettingsResponse> getAlertSettings(@RequestParam Long userId) {
        return ResponseEntity.ok(expirationService.getExpirationSettings(userId));
    }

    /**
     * PUT /alerts/settings?userId={userId}
     * Update alert settings for a user
     */
    @PutMapping("/settings")
    public ResponseEntity<ExpirationSettingsResponse> updateAlertSettings(
            @RequestParam Long userId,
            @RequestBody ExpirationSettingsRequest request) {
        return ResponseEntity.ok(expirationService.updateExpirationSettings(userId, request));
    }

    /**
     * GET /alerts/notifications?userId={userId}
     * Get items that should trigger alerts based on user's settings
     * (This would typically be called by a scheduled job)
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<UpcomingExpirationResponse>> getItemsRequiringAlerts(@RequestParam Long userId) {
        return ResponseEntity.ok(expirationService.getItemsRequiringAlerts(userId));
    }
}
