package com.carmaintenance.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maintenance Record DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecordDTO {

    private Long id;

    @NotNull(message = "Car ID is required")
    private Long carId;

    @NotNull(message = "Maintenance type ID is required")
    private Integer maintenanceTypeId;

    private String maintenanceTypeName;
    private String maintenanceTypeNameAr;

    @NotNull(message = "Service date is required")
    private LocalDate serviceDate;

    @NotNull(message = "Mileage at service is required")
    @Min(value = 0, message = "Mileage must be positive")
    private Integer mileageAtService;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", message = "Cost must be positive")
    private BigDecimal cost;

    private BigDecimal laborCost;

    private BigDecimal partsCost;

    @Size(max = 100, message = "Workshop name must not exceed 100 characters")
    private String workshopName;

    @Size(max = 200, message = "Workshop location must not exceed 200 characters")
    private String workshopLocation;

    @Size(max = 100, message = "Technician name must not exceed 100 characters")
    private String technicianName;

    private String description;

    private String partsUsed;

    private Integer nextServiceMileage;

    private LocalDate nextServiceDate;

    private Integer warrantyMonths;

    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    private String invoiceNumber;

    private String receiptImageUrl;

    private String status;

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Car info
    private String carPlateNumber;
    private String carBrand;
    private String carModel;
}
