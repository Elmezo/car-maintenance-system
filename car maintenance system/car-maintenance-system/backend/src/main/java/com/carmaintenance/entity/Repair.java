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
 * Repair Entity
 * Records of repairs made for failures
 */
@Entity
@Table(name = "repairs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "failure_id", nullable = false)
    private Failure failure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "repair_date", nullable = false)
    private LocalDate repairDate;

    @Column(name = "mileage_at_repair", nullable = false)
    private Integer mileageAtRepair;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cost;

    @Column(name = "labor_cost", precision = 10, scale = 2)
    private BigDecimal laborCost;

    @Column(name = "parts_cost", precision = 10, scale = 2)
    private BigDecimal partsCost;

    @Column(name = "workshop_name", length = 100)
    private String workshopName;

    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "parts_replaced", columnDefinition = "TEXT")
    private String partsReplaced;

    @Column(name = "repair_method", columnDefinition = "TEXT")
    private String repairMethod;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    private RepairStatus status = RepairStatus.completed;

    @Column(name = "is_successful")
    private Boolean isSuccessful = true;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum RepairStatus {
        pending, in_progress, completed, partially_completed
    }
}
