package com.wasteless.backend.controller;

import com.wasteless.backend.dto.donation.DonationCenterResponse;
import com.wasteless.backend.model.DonationCenter;
import com.wasteless.backend.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    /**
     * GET /api/v1/donations/nearby
     * Find donation centers near user's location
     *
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @param radius Search radius in kilometers (default: 10km)
     * @param type Optional filter by type (e.g., "Food Bank", "Shelter", "Community Fridge")
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<DonationCenterResponse>> getNearbyDonationCenters(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radius,
            @RequestParam(required = false) String type,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<DonationCenterResponse> centers = donationService.findNearbyDonationCenters(
                latitude, longitude, radius, type
        );
        return ResponseEntity.ok(centers);
    }

    /**
     * GET /api/v1/donations/city/{city}
     * Find donation centers in a specific city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<DonationCenterResponse>> getDonationCentersByCity(
            @PathVariable String city,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<DonationCenterResponse> centers = donationService.findByCity(city, latitude, longitude);
        return ResponseEntity.ok(centers);
    }

    /**
     * GET /api/v1/donations/all
     * Get all active donation centers
     */
    @GetMapping("/all")
    public ResponseEntity<List<DonationCenterResponse>> getAllDonationCenters(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<DonationCenterResponse> centers = donationService.getAllDonationCenters(latitude, longitude);
        return ResponseEntity.ok(centers);
    }

    /**
     * GET /api/v1/donations/{id}
     * Get specific donation center details
     */
    @GetMapping("/{id}")
    public ResponseEntity<DonationCenterResponse> getDonationCenterById(
            @PathVariable Long id,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DonationCenterResponse center = donationService.getDonationCenterById(id, latitude, longitude);
        return ResponseEntity.ok(center);
    }

    /**
     * POST /api/v1/donations/centers
     * Create a new donation center (Admin only - add role check in production)
     */
    @PostMapping("/centers")
    public ResponseEntity<DonationCenter> createDonationCenter(
            @RequestBody DonationCenter donationCenter,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DonationCenter created = donationService.createDonationCenter(donationCenter);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
