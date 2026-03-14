package com.carmaintenance.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Failure DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailureDTO {

    private Long id;

    @NotNull(message = "Car ID is required")
    private Long carId;

    @NotNull(message = "Failure type ID is required")
    private Integer failureTypeId;

    private String failureTypeName;
    private String failureTypeNameAr;
    private String failureCategory;

    @NotNull(message = "Failure date is required")
    private LocalDate failureDate;

    @NotNull(message = "Mileage at failure is required")
    @Min(value = 0, message = "Mileage must be positive")
    private Integer mileageAtFailure;

    private String severity;

    private String description;

    private String symptoms;

    private String rootCause;

    @Size(max = 50, message = "Weather conditions must not exceed 50 characters")
    private String weatherConditions;

    @Size(max = 50, message = "Driving conditions must not exceed 50 characters")
    private String drivingConditions;

    private Boolean isRecurring;

    private Long parentFailureId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Car info
    private String carPlateNumber;
    private String carBrand;
    private String carModel;

    // Repair info
    private Boolean isRepaired;
    private Integer repairCount;
}
