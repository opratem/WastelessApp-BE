package com.wasteless.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "donation_centers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "address", "city"})
        })
public class DonationCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // "Food Bank", "Shelter", "Community Fridge"

    private String address;
    private String city;
    private String state;

    private Double latitude;
    private Double longitude;

    private String phoneNumber;
    private String email;

    private String openingHours; // e.g., "Mon-Fri: 9AM-5PM"
    private String acceptedItems; // e.g., "Fresh produce, canned goods, dairy"

    private Boolean isActive = true;
    private String website;
}
