package com.carmaintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Dashboard Statistics DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // Overall stats
    private Long totalCars;
    private Long activeCars;
    private Long totalMaintenanceRecords;
    private Long totalFailures;
    private BigDecimal totalMaintenanceCost;
    private BigDecimal totalRepairCost;
    private BigDecimal grandTotalCost;

    // Average stats
    private Double averageMileage;
    private BigDecimal averageMaintenanceCostPerCar;
    private BigDecimal averageRepairCostPerCar;

    // Monthly trends
    private List<MonthlyCostDTO> monthlyCosts;

    // Most common failures
    private List<FailureStatsDTO> topFailures;

    // Maintenance types stats
    private List<MaintenanceStatsDTO> maintenanceStats;

    // Car health distribution
    private Map<String, Long> healthDistribution;

    // Upcoming maintenance
    private List<UpcomingMaintenanceDTO> upcomingMaintenance;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyCostDTO {
        private Integer year;
        private Integer month;
        private BigDecimal maintenanceCost;
        private BigDecimal repairCost;
        private Long maintenanceCount;
        private Long failureCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailureStatsDTO {
        private String failureTypeName;
        private String failureTypeNameAr;
        private Long count;
        private BigDecimal totalCost;
        private BigDecimal averageCost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaintenanceStatsDTO {
        private String maintenanceTypeName;
        private String maintenanceTypeNameAr;
        private Long count;
        private BigDecimal totalCost;
        private BigDecimal averageCost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpcomingMaintenanceDTO {
        private Long carId;
        private String plateNumber;
        private String brand;
        private String model;
        private String maintenanceType;
        private LocalDate dueDate;
        private Integer dueMileage;
        private Integer daysUntilDue;
        private Integer kmUntilDue;
        private String priority;
    }
}
