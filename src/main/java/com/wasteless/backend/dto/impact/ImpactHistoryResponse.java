package com.wasteless.backend.dto.impact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactHistoryResponse {

    private List<MonthlyImpact> monthlyData;
    private ImpactSummaryResponse totalImpact;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyImpact {
        private String month;              // e.g., "January 2024"
        private Double moneySaved;
        private Double moneyWasted;
        private Double netImpact;
        private Double co2Saved;
        private Integer itemsEaten;
        private Integer itemsSaved;
        private Integer itemsWasted;
        private Double wasteReductionPercentage;
    }
}
