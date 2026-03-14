package com.carmaintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Prediction Request DTO
 * Request to Python Analytics Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequestDTO {

    private Long carId;

    private Integer currentMileage;

    private Integer carAge;

    private String engineType;

    private String transmission;

    // Maintenance history
    private List<MaintenanceHistoryItem> maintenanceHistory;

    // Failure history
    private List<FailureHistoryItem> failureHistory;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaintenanceHistoryItem {
        private String maintenanceType;
        private Integer daysSinceLastService;
        private Integer kmSinceLastService;
        private Integer recommendedIntervalKm;
        private Integer recommendedIntervalMonths;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailureHistoryItem {
        private String failureType;
        private String severity;
        private Integer daysSinceFailure;
        private Boolean isRecurring;
    }
}
