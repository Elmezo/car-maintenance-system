package com.carmaintenance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Prediction Entity
 * Stores ML-based predictions for maintenance and failures
 */
@Entity
@Table(name = "predictions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Enumerated(EnumType.STRING)
    @Column(name = "prediction_type", nullable = false)
    private PredictionType predictionType;

    @Column(name = "prediction_date")
    private LocalDateTime predictionDate;

    @Column(name = "predicted_event", nullable = false, length = 100)
    private String predictedEvent;

    @Column(name = "predicted_date")
    private LocalDate predictedDate;

    @Column(name = "predicted_mileage")
    private Integer predictedMileage;

    @Column(precision = 5, scale = 4)
    private BigDecimal probability;

    @Enumerated(EnumType.STRING)
    @Column(name = "confidence_level")
    private ConfidenceLevel confidenceLevel = ConfidenceLevel.medium;

    @Column(name = "contributing_factors", columnDefinition = "TEXT")
    private String contributingFactors;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "model_version", length = 20)
    private String modelVersion;

    @Column(name = "is_accurate")
    private Boolean isAccurate;

    @Column(name = "actual_outcome", columnDefinition = "TEXT")
    private String actualOutcome;

    @Column(name = "feedback_date")
    private LocalDateTime feedbackDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum PredictionType {
        maintenance, failure, cost
    }

    public enum ConfidenceLevel {
        low, medium, high
    }
}
