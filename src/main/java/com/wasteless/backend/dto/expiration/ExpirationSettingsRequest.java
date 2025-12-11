package com.wasteless.backend.dto.expiration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpirationSettingsRequest {
    private Integer daysBeforeExpiryFirstAlert;
    private Integer daysBeforeExpirySecondAlert;
    private Boolean emailNotificationsEnabled;
    private Boolean pushNotificationsEnabled;
    private Boolean alertOnExpiryDay;
}
