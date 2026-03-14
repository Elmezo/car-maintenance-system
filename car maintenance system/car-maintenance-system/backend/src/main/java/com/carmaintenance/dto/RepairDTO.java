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
 * Repair DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairDTO {

    private Long id;

    @NotNull(message = "Failure ID is required")
    private Long failureId;

    @NotNull(message = "Car ID is required")
    private Long carId;

    @NotNull(message = "Repair date is required")
    private LocalDate repairDate;

    @NotNull(message = "Mileage at repair is required")
    @Min(value = 0, message = "Mileage must be positive")
    private Integer mileageAtRepair;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", message = "Cost must be positive")
    private BigDecimal cost;

    private BigDecimal laborCost;

    private BigDecimal partsCost;

    @Size(max = 100, message = "Workshop name must not exceed 100 characters")
    private String workshopName;

    @Size(max = 100, message = "Technician name must not exceed 100 characters")
    private String technicianName;

    private String description;

    private String partsReplaced;

    private String repairMethod;

    private Integer warrantyMonths;

    @Size(max = 50, message = "Invoice number must not exceed 50 characters")
    private String invoiceNumber;

    private String status;

    private Boolean isSuccessful;

    private Boolean followUpRequired;

    private LocalDate followUpDate;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Failure info
    private String failureTypeName;
    private String failureSeverity;
}
