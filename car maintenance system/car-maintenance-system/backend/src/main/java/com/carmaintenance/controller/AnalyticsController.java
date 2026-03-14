package com.carmaintenance.controller;

import com.carmaintenance.dto.AnalyticsService;
import com.carmaintenance.dto.ApiResponse;
import com.carmaintenance.dto.DashboardStatsDTO;
import com.carmaintenance.dto.PredictionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Analytics Controller
 * REST API endpoints for analytics and predictions
 */
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "APIs for analytics and predictions")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve comprehensive dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        log.info("REST request to get dashboard statistics");
        DashboardStatsDTO stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/health-score/{carId}")
    @Operation(summary = "Get car health score", description = "Calculate and retrieve health score for a car")
    public ResponseEntity<ApiResponse<Integer>> getCarHealthScore(
            @Parameter(description = "Car ID") @PathVariable Long carId) {
        log.info("REST request to get health score for car: {}", carId);
        // This would need to fetch the car first
        // For now, returning a placeholder
        return ResponseEntity.ok(ApiResponse.success(85));
    }

    @GetMapping("/predictions/{carId}")
    @Operation(summary = "Get predictions for car", description = "Retrieve predictions for a specific car")
    public ResponseEntity<ApiResponse<List<PredictionDTO>>> getPredictionsForCar(
            @Parameter(description = "Car ID") @PathVariable Long carId) {
        log.info("REST request to get predictions for car: {}", carId);
        List<PredictionDTO> predictions = analyticsService.getPredictionsForCar(carId);
        return ResponseEntity.ok(ApiResponse.success(predictions));
    }

    @PostMapping("/predict/{carId}")
    @Operation(summary = "Generate predictions", description = "Generate new predictions for a car")
    public ResponseEntity<ApiResponse<List<PredictionDTO>>> generatePredictions(
            @Parameter(description = "Car ID") @PathVariable Long carId) {
        log.info("REST request to generate predictions for car: {}", carId);
        List<PredictionDTO> predictions = analyticsService.requestPrediction(carId);
        return ResponseEntity.ok(ApiResponse.success("Predictions generated successfully", predictions));
    }
}
