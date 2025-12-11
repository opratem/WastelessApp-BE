package com.wasteless.backend.service;

import com.wasteless.backend.dto.expiration.ExpirationSettingsRequest;
import com.wasteless.backend.dto.expiration.ExpirationSettingsResponse;
import com.wasteless.backend.dto.expiration.UpcomingExpirationResponse;
import com.wasteless.backend.model.ExpirationSettings;
import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.ExpirationSettingsRepository;
import com.wasteless.backend.repository.InventoryRepository;
import com.wasteless.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpirationService {

    private final InventoryRepository inventoryRepository;
    private final ExpirationSettingsRepository expirationSettingsRepository;
    private final UserRepository userRepository;

    //Get all items expiring within the next N days for a user

    public List<UpcomingExpirationResponse> getUpcomingExpirations(Long userId, Integer daysAhead) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead != null ? daysAhead : 7); // Default 7 days

        List<InventoryItem> items = inventoryRepository.findByUserAndExpiryDateBetween(user, today, futureDate);

        return items.stream()
                .map(UpcomingExpirationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    //Get items expiring today
    public List<UpcomingExpirationResponse> getExpirationsToday(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        List<InventoryItem> items = inventoryRepository.findByUserAndExpiryDateBetween(user, today, today);

        return items.stream()
                .map(UpcomingExpirationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    //Get Critical items (expiring in 0-1 days)
    public List<UpcomingExpirationResponse> getCriticalExpirations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<InventoryItem> items = inventoryRepository.findByUserAndExpiryDateBetween(user, today, tomorrow);

        return items.stream()
                .map(UpcomingExpirationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    //Get Expiration/Alert settings for a user
    public ExpirationSettingsResponse getExpirationSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ExpirationSettings settings = expirationSettingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));

        return ExpirationSettingsResponse.fromEntity(settings);
    }

    //Update Expiration/Alert settings for a user
    public ExpirationSettingsResponse updateExpirationSettings(Long userId, ExpirationSettingsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ExpirationSettings settings = expirationSettingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));

        //Update settings
        if (request.getDaysBeforeExpiryFirstAlert() != null) {
            settings.setDaysBeforeExpiryFirstAlert(request.getDaysBeforeExpiryFirstAlert());
        }
        if (request.getDaysBeforeExpirySecondAlert() != null) {
            settings.setDaysBeforeExpirySecondAlert(request.getDaysBeforeExpirySecondAlert());
        }
        if (request.getEmailNotificationsEnabled() != null) {
            settings.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        }
        if (request.getPushNotificationsEnabled() != null) {
            settings.setPushNotificationsEnabled(request.getPushNotificationsEnabled());
        }
        if (request.getAlertOnExpiryDay() != null) {
            settings.setAlertOnExpiryDay(request.getAlertOnExpiryDay());
        }

        ExpirationSettings saved = expirationSettingsRepository.save(settings);
        return ExpirationSettingsResponse.fromEntity(saved);
    }

    //Create default alert settings for a new user
    private ExpirationSettings createDefaultSettings(User user) {
        ExpirationSettings defaultSettings = ExpirationSettings.builder()
                .user(user)
                .daysBeforeExpiryFirstAlert(3)
                .daysBeforeExpirySecondAlert(1)
                .emailNotificationsEnabled(true)
                .pushNotificationsEnabled(true)
                .alertOnExpiryDay(true)
                .build();

        return expirationSettingsRepository.save(defaultSettings);
    }

    /**
     * Check which items should trigger alerts based on user's alert settings
     * This method would be called by a scheduled task (e.g., daily cron job)
     */
    public List<UpcomingExpirationResponse> getItemsRequiringAlerts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ExpirationSettings settings = expirationSettingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));

        LocalDate today = LocalDate.now();
        LocalDate maxAlertDate = today.plusDays(
                Math.max(settings.getDaysBeforeExpiryFirstAlert(), settings.getDaysBeforeExpirySecondAlert())
        );

        List<InventoryItem> items = inventoryRepository.findByUserAndExpiryDateBetween(user, today, maxAlertDate);

        return items.stream()
                .map(UpcomingExpirationResponse::fromEntity)
                .filter(item -> shouldTriggerAlert(item, settings))
                .collect(Collectors.toList());
    }

    //Determine if an item should trigger an alert based on settings
    private boolean shouldTriggerAlert(UpcomingExpirationResponse item, ExpirationSettings settings) {
        long daysUntilExpiry = item.getDaysUntilExpiry();

        return daysUntilExpiry == settings.getDaysBeforeExpiryFirstAlert() ||
                daysUntilExpiry == settings.getDaysBeforeExpirySecondAlert() ||
                (daysUntilExpiry == 0 && settings.getAlertOnExpiryDay());
    }
}
