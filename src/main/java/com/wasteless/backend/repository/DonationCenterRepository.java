package com.wasteless.backend.repository;

import com.wasteless.backend.model.DonationCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationCenterRepository extends JpaRepository<DonationCenter, Long> {

    List<DonationCenter> findByIsActiveTrue();

    List<DonationCenter> findByTypeAndIsActiveTrue(String type);

    List<DonationCenter> findByCityAndIsActiveTrue(String city);

    List<DonationCenter> findByStateAndIsActiveTrue(String state);

    /**
     * Find donation centers within a radius (using Haversine formula)
     * Note: This is a simple implementation. For production, consider using PostGIS.
     */
    @Query(value = """
        SELECT * FROM donation_centers dc
        WHERE dc.is_active = true
        AND (
            6371 * acos(
                cos(radians(:latitude)) * cos(radians(dc.latitude)) *
                cos(radians(dc.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(dc.latitude))
            )
        ) <= :radiusKm
        ORDER BY (
            6371 * acos(
                cos(radians(:latitude)) * cos(radians(dc.latitude)) *
                cos(radians(dc.longitude) - radians(:longitude)) +
                sin(radians(:latitude)) * sin(radians(dc.latitude))
            )
        ) ASC
        """, nativeQuery = true)
    List<DonationCenter> findNearbyDonationCenters(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm
    );
}
