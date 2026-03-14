package com.carmaintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Prediction DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionDTO {

    private Long id;

    private Long carId;

    private String predictionType;

    private LocalDateTime predictionDate;

    private String predictedEvent;

    private LocalDate predictedDate;

    private Integer predictedMileage;

    private BigDecimal probability;

    private String confidenceLevel;

    private String contributingFactors;

    private String recommendations;

    private String modelVersion;

    private Boolean isAccurate;

    private String actualOutcome;

    private LocalDateTime feedbackDate;

    private LocalDateTime createdAt;

    // Car info
    private String carPlateNumber;
    private String carBrand;
    private String carModel;
}
