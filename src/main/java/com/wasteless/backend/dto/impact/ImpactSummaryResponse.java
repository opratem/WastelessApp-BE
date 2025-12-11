package com.wasteless.backend.dto.impact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactSummaryResponse {

    // Financial Impact
    private Double moneySaved;        // Total money saved in Naira
    private Double moneyWasted;       // Total money wasted in Naira
    private Double netImpact;         // Net financial impact (saved - wasted)

    // Environmental Impact
    private Double co2Saved;          // CO2 saved in kg
    private Double co2Wasted;         // CO2 from wasted food in kg

    // Volume Impact
    private Integer itemsEaten;       // Number of items eaten/consumed
    private Integer itemsSaved;       // Number of items saved from waste (same as itemsEaten)
    private Integer itemsWasted;      // Number of items wasted
    private Integer itemsActive;      // Number of items still in inventory
    private Integer totalItems;       // Total items processed

    // Percentage
    private Double wasteReductionPercentage;  // % of items saved from waste

    // Period
    private String period;            // e.g., "November 2025", "Last 30 Days"
}
