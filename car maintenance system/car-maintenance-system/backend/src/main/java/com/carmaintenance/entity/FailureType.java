package com.carmaintenance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Failure Type Entity
 * Defines types of vehicle failures
 */
@Entity
@Table(name = "failure_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailureType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_ar", length = 100)
    private String nameAr;

    @Column(length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "common_causes", columnDefinition = "TEXT")
    private String commonCauses;

    @Column(name = "prevention_tips", columnDefinition = "TEXT")
    private String preventionTips;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
