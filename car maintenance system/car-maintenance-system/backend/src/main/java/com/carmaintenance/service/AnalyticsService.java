package com.carmaintenance.service;

import com.carmaintenance.dto.*;
import com.carmaintenance.entity.*;
import com.carmaintenance.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics Service
 * Business logic for analytics and predictions
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {

    private final CarRepository carRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final FailureRepository failureRepository;
    private final RepairRepository repairRepository;
    private final MaintenanceTypeRepository maintenanceTypeRepository;
    private final PredictionRepository predictionRepository;

    @Value("${analytics.service.url}")
    private String analyticsServiceUrl;

    private final WebClient webClient;

    /**
     * Get dashboard statistics
     */
    @Cacheable(value = "dashboardStats")
    public DashboardStatsDTO getDashboardStats() {
        log.info("Calculating dashboard statistics");

        DashboardStatsDTO.DashboardStatsDTOBuilder builder = DashboardStatsDTO.builder();

        // Basic counts
        List<Car> allCars = carRepository.findAll();
        List<Car> activeCars = allCars.stream()
                .filter(c -> c.getStatus() == Car.CarStatus.active)
                .collect(Collectors.toList());

        builder.totalCars((long) allCars.size());
        builder.activeCars((long) activeCars.size());

        // Maintenance stats
        List<MaintenanceRecord> allMaintenance = maintenanceRecordRepository.findAll();
        builder.totalMaintenanceRecords((long) allMaintenance.size());

        BigDecimal totalMaintenanceCost = allMaintenance.stream()
                .map(MaintenanceRecord::getCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        builder.totalMaintenanceCost(totalMaintenanceCost);

        // Failure stats
        List<Failure> allFailures = failureRepository.findAll();
        builder.totalFailures((long) allFailures.size());

        // Repair costs
        List<Repair> allRepairs = repairRepository.findAll();
        BigDecimal totalRepairCost = allRepairs.stream()
                .map(Repair::getCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        builder.totalRepairCost(totalRepairCost);
        builder.grandTotalCost(totalMaintenanceCost.add(totalRepairCost));

        // Average stats
        if (!activeCars.isEmpty()) {
            Double avgMileage = carRepository.getAverageMileage();
            builder.averageMileage(avgMileage);

            if (!allCars.isEmpty()) {
                builder.averageMaintenanceCostPerCar(totalMaintenanceCost.divide(
                        BigDecimal.valueOf(allCars.size()), 2, RoundingMode.HALF_UP));
                builder.averageRepairCostPerCar(totalRepairCost.divide(
                        BigDecimal.valueOf(allCars.size()), 2, RoundingMode.HALF_UP));
            }
        }

        // Monthly costs
        builder.monthlyCosts(getMonthlyCosts());

        // Top failures
        builder.topFailures(getTopFailures(6));

        // Maintenance stats
        builder.maintenanceStats(getMaintenanceStats());

        // Health distribution
        builder.healthDistribution(getHealthDistribution(activeCars));

        // Upcoming maintenance
        builder.upcomingMaintenance(getUpcomingMaintenance(activeCars));

        return builder.build();
    }

    /**
     * Get monthly cost trends
     */
    private List<DashboardStatsDTO.MonthlyCostDTO> getMonthlyCosts() {
        LocalDate startDate = LocalDate.now().minusMonths(12);
        Map<String, DashboardStatsDTO.MonthlyCostDTO> monthlyData = new TreeMap<>();

        // Initialize last 12 months
        for (int i = 0; i < 12; i++) {
            LocalDate date = LocalDate.now().minusMonths(i);
            String key = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            monthlyData.put(key, DashboardStatsDTO.MonthlyCostDTO.builder()
                    .year(date.getYear())
                    .month(date.getMonthValue())
                    .maintenanceCost(BigDecimal.ZERO)
                    .repairCost(BigDecimal.ZERO)
                    .maintenanceCount(0L)
                    .failureCount(0L)
                    .build());
        }

        // Aggregate maintenance costs
        maintenanceRecordRepository.findByServiceDateBetween(startDate, LocalDate.now())
                .forEach(m -> {
                    String key = m.getServiceDate().getYear() + "-" + 
                            String.format("%02d", m.getServiceDate().getMonthValue());
                    DashboardStatsDTO.MonthlyCostDTO dto = monthlyData.get(key);
                    if (dto != null) {
                        dto.setMaintenanceCost(dto.getMaintenanceCost().add(m.getCost()));
                        dto.setMaintenanceCount(dto.getMaintenanceCount() + 1);
                    }
                });

        // Aggregate repair costs
        repairRepository.findByRepairDateBetween(startDate, LocalDate.now())
                .forEach(r -> {
                    String key = r.getRepairDate().getYear() + "-" + 
                            String.format("%02d", r.getRepairDate().getMonthValue());
                    DashboardStatsDTO.MonthlyCostDTO dto = monthlyData.get(key);
                    if (dto != null) {
                        dto.setRepairCost(dto.getRepairCost().add(r.getCost()));
                        dto.setFailureCount(dto.getFailureCount() + 1);
                    }
                });

        return new ArrayList<>(monthlyData.values());
    }

    /**
     * Get top failures
     */
    private List<DashboardStatsDTO.FailureStatsDTO> getTopFailures(int months) {
        LocalDate startDate = LocalDate.now().minusMonths(months);
        List<Object[]> failureData = failureRepository.findMostCommonFailures(startDate);

        return failureData.stream()
                .limit(10)
                .map(row -> {
                    String name = (String) row[0];
                    Long count = (Long) row[1];

                    // Find failure type to get Arabic name
                    String nameAr = failureRepository.findAll().stream()
                            .filter(f -> f.getFailureType().getName().equals(name))
                            .findFirst()
                            .map(f -> f.getFailureType().getNameAr())
                            .orElse(null);

                    // Calculate costs
                    BigDecimal totalCost = failureRepository.findAll().stream()
                            .filter(f -> f.getFailureType().getName().equals(name) && 
                                        f.getFailureDate().isAfter(startDate))
                            .flatMap(f -> f.getRepairs().stream())
                            .map(Repair::getCost)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal avgCost = count > 0 ? 
                            totalCost.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : 
                            BigDecimal.ZERO;

                    return DashboardStatsDTO.FailureStatsDTO.builder()
                            .failureTypeName(name)
                            .failureTypeNameAr(nameAr)
                            .count(count)
                            .totalCost(totalCost)
                            .averageCost(avgCost)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Get maintenance type statistics
     */
    private List<DashboardStatsDTO.MaintenanceStatsDTO> getMaintenanceStats() {
        List<Object[]> maintenanceData = maintenanceRecordRepository.countByMaintenanceType();

        return maintenanceData.stream()
                .limit(10)
                .map(row -> {
                    MaintenanceType type = (MaintenanceType) row[0];
                    Long count = (Long) row[1];

                    Double avgCost = maintenanceRecordRepository.getAverageCostByType(type.getId());
                    BigDecimal totalCost = maintenanceRecordRepository.findAll().stream()
                            .filter(m -> m.getMaintenanceType().getId().equals(type.getId()))
                            .map(MaintenanceRecord::getCost)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return DashboardStatsDTO.MaintenanceStatsDTO.builder()
                            .maintenanceTypeName(type.getName())
                            .maintenanceTypeNameAr(type.getNameAr())
                            .count(count)
                            .totalCost(totalCost)
                            .averageCost(avgCost != null ? BigDecimal.valueOf(avgCost) : BigDecimal.ZERO)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Get health score distribution
     */
    private Map<String, Long> getHealthDistribution(List<Car> cars) {
        Map<String, Long> distribution = new LinkedHashMap<>();
        distribution.put("excellent", 0L);
        distribution.put("good", 0L);
        distribution.put("fair", 0L);
        distribution.put("poor", 0L);
        distribution.put("critical", 0L);

        for (Car car : cars) {
            int score = calculateHealthScore(car);
            String category = getHealthCategory(score);
            distribution.merge(category, 1L, Long::sum);
        }

        return distribution;
    }

    /**
     * Get upcoming maintenance
     */
    private List<DashboardStatsDTO.UpcomingMaintenanceDTO> getUpcomingMaintenance(List<Car> cars) {
        List<DashboardStatsDTO.UpcomingMaintenanceDTO> upcoming = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Car car : cars) {
            List<MaintenanceType> types = maintenanceTypeRepository.findByIsActiveTrue();

            for (MaintenanceType type : types) {
                // Get last maintenance of this type
                List<MaintenanceRecord> records = maintenanceRecordRepository
                        .findByCarAndType(car.getId(), type.getId());

                LocalDate lastServiceDate = records.isEmpty() ? null : records.get(0).getServiceDate();
                Integer lastServiceMileage = records.isEmpty() ? null : records.get(0).getMileageAtService();

                // Check if due
                boolean isDue = false;
                LocalDate dueDate = null;
                Integer dueMileage = null;
                Integer daysUntilDue = null;
                Integer kmUntilDue = null;

                // Check by date
                if (type.getRecommendedIntervalMonths() != null && lastServiceDate != null) {
                    dueDate = lastServiceDate.plusMonths(type.getRecommendedIntervalMonths());
                    daysUntilDue = (int) java.time.temporal.ChronoUnit.DAYS.between(today, dueDate);
                    if (daysUntilDue <= 30) isDue = true;
                }

                // Check by mileage
                if (type.getRecommendedIntervalKm() != null && lastServiceMileage != null) {
                    dueMileage = lastServiceMileage + type.getRecommendedIntervalKm();
                    kmUntilDue = dueMileage - car.getCurrentMileage();
                    if (kmUntilDue <= 1000) isDue = true;
                }

                // If never serviced
                if (lastServiceDate == null) {
                    isDue = true;
                    daysUntilDue = 0;
                    kmUntilDue = 0;
                }

                if (isDue) {
                    upcoming.add(DashboardStatsDTO.UpcomingMaintenanceDTO.builder()
                            .carId(car.getId())
                            .plateNumber(car.getPlateNumber())
                            .brand(car.getBrand())
                            .model(car.getModel())
                            .maintenanceType(type.getName())
                            .dueDate(dueDate)
                            .dueMileage(dueMileage)
                            .daysUntilDue(daysUntilDue)
                            .kmUntilDue(kmUntilDue)
                            .priority(type.getPriority().name())
                            .build());
                }
            }
        }

        // Sort by priority and due date
        upcoming.sort((a, b) -> {
            int priorityCompare = getPriorityOrder(a.getPriority()) - getPriorityOrder(b.getPriority());
            if (priorityCompare != 0) return priorityCompare;
            if (a.getDaysUntilDue() != null && b.getDaysUntilDue() != null) {
                return a.getDaysUntilDue().compareTo(b.getDaysUntilDue());
            }
            return 0;
        });

        return upcoming.stream().limit(20).collect(Collectors.toList());
    }

    private int getPriorityOrder(String priority) {
        return switch (priority) {
            case "critical" -> 0;
            case "high" -> 1;
            case "medium" -> 2;
            default -> 3;
        };
    }

    /**
     * Calculate health score for a car
     */
    public int calculateHealthScore(Car car) {
        int score = 100;

        // Deduct for overdue maintenance (max -25)
        List<MaintenanceRecord> overdue = maintenanceRecordRepository.findUpcomingMaintenance(LocalDate.now()).stream()
                .filter(m -> m.getCar().getId().equals(car.getId()))
                .collect(Collectors.toList());
        score -= Math.min(overdue.size() * 5, 25);

        // Deduct for recent failures (max -25)
        long recentFailures = failureRepository.countRecentFailuresByCar(car.getId(), LocalDate.now().minusMonths(6));
        score -= Math.min((int) (recentFailures * 5), 25);

        // Deduct for car age (max -25)
        int ageDeduction = Math.min(car.getCarAge() * 2, 25);
        score -= ageDeduction;

        // Deduct for high mileage (max -25)
        if (car.getCurrentMileage() > 100000) {
            int mileageDeduction = Math.min((car.getCurrentMileage() - 100000) / 10000, 25);
            score -= mileageDeduction;
        }

        return Math.max(score, 0);
    }

    private String getHealthCategory(int score) {
        if (score >= 90) return "excellent";
        if (score >= 75) return "good";
        if (score >= 50) return "fair";
        if (score >= 25) return "poor";
        return "critical";
    }

    /**
     * Get predictions for a car
     */
    public List<PredictionDTO> getPredictionsForCar(Long carId) {
        return predictionRepository.findByCarIdOrderByPredictionDateDesc(carId).stream()
                .map(this::mapPredictionToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Request prediction from Python analytics service
     */
    @Transactional
    public List<PredictionDTO> requestPrediction(Long carId) {
        log.info("Requesting prediction for car ID: {}", carId);

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        // Build prediction request
        PredictionRequestDTO request = buildPredictionRequest(car);

        try {
            // Call Python analytics service
            PredictionDTO[] predictions = webClient.post()
                    .uri(analyticsServiceUrl + "/predict")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PredictionDTO[].class)
                    .block();

            if (predictions != null) {
                return Arrays.asList(predictions);
            }
        } catch (Exception e) {
            log.error("Error calling analytics service: {}", e.getMessage());
        }

        // Fallback to rule-based predictions
        return generateRuleBasedPredictions(car);
    }

    private PredictionRequestDTO buildPredictionRequest(Car car) {
        PredictionRequestDTO request = PredictionRequestDTO.builder()
                .carId(car.getId())
                .currentMileage(car.getCurrentMileage())
                .carAge(car.getCarAge())
                .engineType(car.getEngineType().name())
                .transmission(car.getTransmission().name())
                .build();

        // Add maintenance history
        List<MaintenanceRecord> records = maintenanceRecordRepository.findByCarIdOrderByServiceDateDesc(car.getId());
        request.setMaintenanceHistory(records.stream()
                .map(m -> PredictionRequestDTO.MaintenanceHistoryItem.builder()
                        .maintenanceType(m.getMaintenanceType().getName())
                        .daysSinceLastService((int) java.time.temporal.ChronoUnit.DAYS.between(
                                m.getServiceDate(), LocalDate.now()))
                        .kmSinceLastService(car.getCurrentMileage() - m.getMileageAtService())
                        .recommendedIntervalKm(m.getMaintenanceType().getRecommendedIntervalKm())
                        .recommendedIntervalMonths(m.getMaintenanceType().getRecommendedIntervalMonths())
                        .build())
                .collect(Collectors.toList()));

        // Add failure history
        List<Failure> failures = failureRepository.findByCarIdOrderByFailureDateDesc(car.getId());
        request.setFailureHistory(failures.stream()
                .map(f -> PredictionRequestDTO.FailureHistoryItem.builder()
                        .failureType(f.getFailureType().getName())
                        .severity(f.getSeverity().name())
                        .daysSinceFailure((int) java.time.temporal.ChronoUnit.DAYS.between(
                                f.getFailureDate(), LocalDate.now()))
                        .isRecurring(f.getIsRecurring())
                        .build())
                .collect(Collectors.toList()));

        return request;
    }

    private List<PredictionDTO> generateRuleBasedPredictions(Car car) {
        List<PredictionDTO> predictions = new ArrayList<>();

        // Predict next oil change
        List<MaintenanceRecord> oilChanges = maintenanceRecordRepository.findByCarAndType(car.getId(), 1);
        if (!oilChanges.isEmpty()) {
            MaintenanceRecord lastOilChange = oilChanges.get(0);
            int kmSinceService = car.getCurrentMileage() - lastOilChange.getMileageAtService();
            int kmUntilNext = 5000 - kmSinceService;

            if (kmUntilNext < 2000) {
                predictions.add(PredictionDTO.builder()
                        .carId(car.getId())
                        .predictionType("maintenance")
                        .predictedEvent("Oil Change")
                        .predictedMileage(car.getCurrentMileage() + kmUntilNext)
                        .predictedDate(LocalDate.now().plusDays((int)(kmUntilNext / 50.0))) // Assume 50km/day
                        .probability(BigDecimal.valueOf(Math.max(0.9 - (kmUntilNext * 0.0001), 0.5)))
                        .confidenceLevel(kmUntilNext < 500 ? "high" : "medium")
                        .recommendations("Schedule oil change within the next " + kmUntilNext + " km")
                        .build());
            }
        }

        // Predict brake pad replacement
        List<MaintenanceRecord> brakeServices = maintenanceRecordRepository.findByCarAndType(car.getId(), 6);
        if (!brakeServices.isEmpty()) {
            MaintenanceRecord lastBrakeService = brakeServices.get(0);
            int kmSinceService = car.getCurrentMileage() - lastBrakeService.getMileageAtService();
            int kmUntilNext = 40000 - kmSinceService;

            if (kmUntilNext < 5000) {
                predictions.add(PredictionDTO.builder()
                        .carId(car.getId())
                        .predictionType("maintenance")
                        .predictedEvent("Brake Pads Replacement")
                        .predictedMileage(car.getCurrentMileage() + kmUntilNext)
                        .probability(BigDecimal.valueOf(0.8))
                        .confidenceLevel("medium")
                        .recommendations("Inspect brake pads and schedule replacement")
                        .build());
            }
        }

        // Predict potential failures based on history
        List<Failure> recentFailures = failureRepository.findByCarIdOrderByFailureDateDesc(car.getId());
        for (Failure failure : recentFailures) {
            if (failure.getIsRecurring() || failure.getSeverity() == Failure.Severity.major) {
                predictions.add(PredictionDTO.builder()
                        .carId(car.getId())
                        .predictionType("failure")
                        .predictedEvent(failure.getFailureType().getName() + " - Recurring Issue")
                        .predictedDate(LocalDate.now().plusMonths(3))
                        .probability(BigDecimal.valueOf(0.7))
                        .confidenceLevel("medium")
                        .contributingFactors("Previous " + failure.getFailureType().getName() + " at " + failure.getMileageAtFailure() + " km")
                        .recommendations("Monitor closely and consider preventive maintenance")
                        .build());
            }
        }

        return predictions;
    }

    private PredictionDTO mapPredictionToDTO(Prediction prediction) {
        return PredictionDTO.builder()
                .id(prediction.getId())
                .carId(prediction.getCar().getId())
                .predictionType(prediction.getPredictionType().name())
                .predictionDate(prediction.getPredictionDate())
                .predictedEvent(prediction.getPredictedEvent())
                .predictedDate(prediction.getPredictedDate())
                .predictedMileage(prediction.getPredictedMileage())
                .probability(prediction.getProbability())
                .confidenceLevel(prediction.getConfidenceLevel().name())
                .contributingFactors(prediction.getContributingFactors())
                .recommendations(prediction.getRecommendations())
                .modelVersion(prediction.getModelVersion())
                .isAccurate(prediction.getIsAccurate())
                .actualOutcome(prediction.getActualOutcome())
                .feedbackDate(prediction.getFeedbackDate())
                .createdAt(prediction.getCreatedAt())
                .carPlateNumber(prediction.getCar().getPlateNumber())
                .carBrand(prediction.getCar().getBrand())
                .carModel(prediction.getCar().getModel())
                .build();
    }
}
