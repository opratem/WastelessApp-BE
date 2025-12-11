package com.wasteless.backend.dto.expiration;

import com.wasteless.backend.model.ExpirationSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpirationSettingsResponse {
    private Long id;
    private Long userId;
    private Integer daysBeforeExpiryFirstAlert;
    private Integer daysBeforeExpirySecondAlert;
    private Boolean emailNotificationsEnabled;
    private Boolean pushNotificationsEnabled;
    private Boolean alertOnExpiryDate;

    public static ExpirationSettingsResponse fromEntity(ExpirationSettings expirationSettings) {
        return ExpirationSettingsResponse.builder()
                .id(expirationSettings.getId())
                .userId(expirationSettings.getUser().getId())
                .daysBeforeExpiryFirstAlert(expirationSettings.getDaysBeforeExpiryFirstAlert())
                .daysBeforeExpirySecondAlert(expirationSettings.getDaysBeforeExpirySecondAlert())
                .emailNotificationsEnabled(expirationSettings.getEmailNotificationsEnabled())
                .pushNotificationsEnabled(expirationSettings.getPushNotificationsEnabled())
                .alertOnExpiryDate(expirationSettings.getAlertOnExpiryDay())
                .build();
    }
}
