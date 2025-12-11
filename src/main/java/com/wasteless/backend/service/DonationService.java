package com.wasteless.backend.service;

import com.wasteless.backend.dto.donation.DonationCenterResponse;
import com.wasteless.backend.model.DonationCenter;
import com.wasteless.backend.repository.DonationCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationCenterRepository donationCenterRepository;

    /**
     * Find donation centers near a given location
     */
    public List<DonationCenterResponse> findNearbyDonationCenters(
            Double userLatitude,
            Double userLongitude,
            Double radiusKm,
            String type
    ) {
        List<DonationCenter> centers;

        if (type != null && !type.isEmpty()) {
            // First filter by location, then by type in Java
            centers = donationCenterRepository.findNearbyDonationCenters(
                            userLatitude, userLongitude, radiusKm
                    ).stream()
                    .filter(center -> center.getType().equalsIgnoreCase(type))
                    .toList();
        } else {
            centers = donationCenterRepository.findNearbyDonationCenters(
                    userLatitude, userLongitude, radiusKm
            );
        }

        return centers.stream()
                .map(center -> mapToResponse(center, userLatitude, userLongitude))
                .collect(Collectors.toList());
    }

    /**
     * Get all donation centers in a specific city
     */
    public List<DonationCenterResponse> findByCity(String city, Double userLat, Double userLon) {
        List<DonationCenter> centers = donationCenterRepository.findByCityAndIsActiveTrue(city);
        return centers.stream()
                .map(center -> mapToResponse(center, userLat, userLon))
                .collect(Collectors.toList());
    }

    /**
     * Get all donation centers
     */
    public List<DonationCenterResponse> getAllDonationCenters(Double userLat, Double userLon) {
        List<DonationCenter> centers = donationCenterRepository.findByIsActiveTrue();
        return centers.stream()
                .map(center -> mapToResponse(center, userLat, userLon))
                .sorted((a, b) -> {
                    // Handle null distances - put nulls at the end
                    if (a.getDistanceKm() == null && b.getDistanceKm() == null) return 0;
                    if (a.getDistanceKm() == null) return 1;
                    if (b.getDistanceKm() == null) return -1;
                    return Double.compare(a.getDistanceKm(), b.getDistanceKm());
                })
                .collect(Collectors.toList());
    }

    /**
     * Get donation center by ID
     */
    public DonationCenterResponse getDonationCenterById(Long id, Double userLat, Double userLon) {
        DonationCenter center = donationCenterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation center not found with id: " + id));
        return mapToResponse(center, userLat, userLon);
    }

    /**
     * Create a new donation center (Admin function)
     */
    public DonationCenter createDonationCenter(DonationCenter donationCenter) {
        return donationCenterRepository.save(donationCenter);
    }

    /**
     * Map entity to response DTO
     */
    private DonationCenterResponse mapToResponse(
            DonationCenter center,
            Double userLatitude,
            Double userLongitude
    ) {
        Double distance = null;
        if (userLatitude != null && userLongitude != null) {
            distance = calculateDistance(
                    userLatitude, userLongitude,
                    center.getLatitude(), center.getLongitude()
            );
        }

        return DonationCenterResponse.builder()
                .id(center.getId())
                .name(center.getName())
                .type(center.getType())
                .address(center.getAddress())
                .city(center.getCity())
                .state(center.getState())
                .latitude(center.getLatitude())
                .longitude(center.getLongitude())
                .distanceKm(distance != null ? Math.round(distance * 10.0) / 10.0 : null)
                .phoneNumber(center.getPhoneNumber())
                .email(center.getEmail())
                .openingHours(center.getOpeningHours())
                .acceptedItems(center.getAcceptedItems())
                .website(center.getWebsite())
                .isCurrentlyOpen(isCurrentlyOpen(center.getOpeningHours()))
                .build();
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     * Returns distance in kilometers
     */
    private double calculateDistance(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        final int EARTH_RADIUS_KM = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Check if donation center is currently open based on opening hours
     * Simple implementation - can be enhanced with better parsing
     */
    private Boolean isCurrentlyOpen(String openingHours) {
        if (openingHours == null || openingHours.isEmpty()) {
            return null; // Unknown
        }

        // This is a simplified check
        // In production, you'd want to parse the opening hours string properly
        LocalTime now = LocalTime.now();
        DayOfWeek today = DayOfWeek.from(java.time.LocalDate.now());

        // Basic check: if it contains "24/7" or "Always Open"
        if (openingHours.toLowerCase().contains("24/7") ||
                openingHours.toLowerCase().contains("always open")) {
            return true;
        }

        // For MVP, return null (unknown) - can be enhanced later
        return null;
    }
}
