package com.carmaintenance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Maintenance Record Entity
 * Records of all maintenance activities performed on cars
 */
@Entity
@Table(name = "maintenance_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_type_id", nullable = false)
    private MaintenanceType maintenanceType;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "mileage_at_service", nullable = false)
    private Integer mileageAtService;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cost;

    @Column(name = "labor_cost", precision = 10, scale = 2)
    private BigDecimal laborCost;

    @Column(name = "parts_cost", precision = 10, scale = 2)
    private BigDecimal partsCost;

    @Column(name = "workshop_name", length = 100)
    private String workshopName;

    @Column(name = "workshop_location", length = 200)
    private String workshopLocation;

    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "parts_used", columnDefinition = "TEXT")
    private String partsUsed;

    @Column(name = "next_service_mileage")
    private Integer nextServiceMileage;

    @Column(name = "next_service_date")
    private LocalDate nextServiceDate;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Column(name = "receipt_image_url", length = 500)
    private String receiptImageUrl;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status = MaintenanceStatus.completed;

    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum MaintenanceStatus {
        scheduled, in_progress, completed, cancelled
    }

    // Helper methods
    @Transient
    public boolean isOverdue() {
        if (nextServiceMileage != null && car != null) {
            return car.getCurrentMileage() >= nextServiceMileage;
        }
        if (nextServiceDate != null) {
            return LocalDate.now().isAfter(nextServiceDate);
        }
        return false;
    }

    @Transient
    public int getDaysUntilNextService() {
        if (nextServiceDate != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextServiceDate);
        }
        return Integer.MAX_VALUE;
    }
}
