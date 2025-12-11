package com.wasteless.backend.dto.donation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationCenterResponse {

    private Long id;
    private String name;
    private String type;

    private String address;
    private String city;
    private String state;

    private Double latitude;
    private Double longitude;
    private Double distanceKm; // Distance from user's location

    private String phoneNumber;
    private String email;
    private String openingHours;
    private String acceptedItems;
    private String website;

    private Boolean isCurrentlyOpen; // Real-time open/closed status
}
