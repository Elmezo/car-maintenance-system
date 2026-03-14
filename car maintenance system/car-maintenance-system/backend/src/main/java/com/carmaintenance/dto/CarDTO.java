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
 * Car DTO
 * Data Transfer Object for Car operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarDTO {

    private Long id;

    @NotBlank(message = "Plate number is required")
    @Size(max = 20, message = "Plate number must not exceed 20 characters")
    private String plateNumber;

    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand must not exceed 50 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be at least 1900")
    @Max(value = 2100, message = "Year must not exceed 2100")
    private Integer year;

    @Size(max = 30, message = "Color must not exceed 30 characters")
    private String color;

    @Size(max = 17, message = "VIN must not exceed 17 characters")
    private String vin;

    @Min(value = 0, message = "Mileage must be positive")
    private Integer currentMileage;

    private String engineType;

    private String transmission;

    private BigDecimal fuelCapacity;

    @Size(max = 100, message = "Owner name must not exceed 100 characters")
    private String ownerName;

    @Size(max = 20, message = "Owner phone must not exceed 20 characters")
    private String ownerPhone;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Owner email must not exceed 100 characters")
    private String ownerEmail;

    private LocalDate purchaseDate;

    private LocalDate warrantyExpiry;

    private String imageUrl;

    private String status;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Computed fields
    private Integer carAge;

    private Boolean warrantyActive;

    private Long maintenanceCount;

    private Long failureCount;

    private BigDecimal totalMaintenanceCost;

    private BigDecimal totalRepairCost;

    private Integer healthScore;
}
