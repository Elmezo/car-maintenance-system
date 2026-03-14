package com.carmaintenance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Maintenance Type Entity
 * Defines types of maintenance services available
 */
@Entity
@Table(name = "maintenance_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_ar", length = 100)
    private String nameAr;

    @Enumerated(EnumType.STRING)
    private MaintenanceCategory category = MaintenanceCategory.routine;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "recommended_interval_km")
    private Integer recommendedIntervalKm;

    @Column(name = "recommended_interval_months")
    private Integer recommendedIntervalMonths;

    @Column(name = "estimated_duration_hours", precision = 5, scale = 2)
    private BigDecimal estimatedDurationHours;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.medium;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum MaintenanceCategory {
        routine, preventive, corrective, emergency
    }

    public enum Priority {
        low, medium, high, critical
    }
}
