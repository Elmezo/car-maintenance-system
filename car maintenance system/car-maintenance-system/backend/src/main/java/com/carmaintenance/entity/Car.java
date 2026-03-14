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
import java.util.ArrayList;
import java.util.List;

/**
 * Car Entity
 * Represents a vehicle in the maintenance system
 */
@Entity
@Table(name = "cars")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 30)
    private String color;

    @Column(name = "vin", unique = true, length = 17)
    private String vin;

    @Column(name = "current_mileage")
    private Integer currentMileage = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type")
    private EngineType engineType = EngineType.petrol;

    @Enumerated(EnumType.STRING)
    private Transmission transmission = Transmission.automatic;

    @Column(name = "fuel_capacity", precision = 10, scale = 2)
    private BigDecimal fuelCapacity;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "owner_phone", length = 20)
    private String ownerPhone;

    @Column(name = "owner_email", length = 100)
    private String ownerEmail;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "warranty_expiry")
    private LocalDate warrantyExpiry;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private CarStatus status = CarStatus.active;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MaintenanceRecord> maintenanceRecords = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Failure> failures = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Prediction> predictions = new ArrayList<>();

    // Enums
    public enum EngineType {
        petrol, diesel, electric, hybrid
    }

    public enum Transmission {
        manual, automatic, cvt
    }

    public enum CarStatus {
        active, inactive, sold
    }

    // Helper methods
    @Transient
    public int getCarAge() {
        return LocalDate.now().getYear() - year;
    }

    @Transient
    public boolean isWarrantyActive() {
        return warrantyExpiry != null && warrantyExpiry.isAfter(LocalDate.now());
    }
}
