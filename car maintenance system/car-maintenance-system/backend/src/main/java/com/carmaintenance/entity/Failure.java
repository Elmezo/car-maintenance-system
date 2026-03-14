package com.carmaintenance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Failure Entity
 * Records of vehicle failures/breakdowns
 */
@Entity
@Table(name = "failures")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Failure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "failure_type_id", nullable = false)
    private FailureType failureType;

    @Column(name = "failure_date", nullable = false)
    private LocalDate failureDate;

    @Column(name = "mileage_at_failure", nullable = false)
    private Integer mileageAtFailure;

    @Enumerated(EnumType.STRING)
    private Severity severity = Severity.moderate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "weather_conditions", length = 50)
    private String weatherConditions;

    @Column(name = "driving_conditions", length = 50)
    private String drivingConditions;

    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_failure_id")
    private Failure parentFailure;

    @OneToMany(mappedBy = "failure", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Repair> repairs = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum Severity {
        minor, moderate, major, critical
    }

    // Helper methods
    @Transient
    public boolean isRepaired() {
        return repairs != null && repairs.stream()
                .anyMatch(r -> r.getStatus() == Repair.RepairStatus.completed && r.getIsSuccessful());
    }
}
