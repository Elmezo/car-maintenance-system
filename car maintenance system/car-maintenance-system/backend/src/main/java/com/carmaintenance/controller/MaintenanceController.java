package com.carmaintenance.controller;

import com.carmaintenance.dto.ApiResponse;
import com.carmaintenance.dto.MaintenanceRecordDTO;
import com.carmaintenance.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Maintenance Controller
 * REST API endpoints for maintenance records
 */
@RestController
@RequestMapping("/maintenance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Maintenance Management", description = "APIs for managing maintenance records")
@CrossOrigin(origins = "*")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    @Operation(summary = "Create maintenance record", description = "Add a new maintenance record")
    public ResponseEntity<ApiResponse<MaintenanceRecordDTO>> createMaintenanceRecord(
            @Valid @RequestBody MaintenanceRecordDTO dto) {
        log.info("REST request to create maintenance record for car: {}", dto.getCarId());
        MaintenanceRecordDTO created = maintenanceService.createMaintenanceRecord(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Maintenance record created successfully", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get maintenance record by ID", description = "Retrieve maintenance record details")
    public ResponseEntity<ApiResponse<MaintenanceRecordDTO>> getMaintenanceRecordById(
            @Parameter(description = "Maintenance record ID") @PathVariable Long id) {
        log.info("REST request to get maintenance record: {}", id);
        MaintenanceRecordDTO record = maintenanceService.getMaintenanceRecordById(id);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @GetMapping("/car/{carId}")
    @Operation(summary = "Get maintenance records by car", description = "Retrieve all maintenance records for a car")
    public ResponseEntity<ApiResponse<List<MaintenanceRecordDTO>>> getMaintenanceRecordsByCar(
            @Parameter(description = "Car ID") @PathVariable Long carId) {
        log.info("REST request to get maintenance records for car: {}", carId);
        List<MaintenanceRecordDTO> records = maintenanceService.getMaintenanceRecordsByCar(carId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/car/{carId}/paged")
    @Operation(summary = "Get maintenance records with pagination", description = "Retrieve maintenance records with pagination")
    public ResponseEntity<ApiResponse<Page<MaintenanceRecordDTO>>> getMaintenanceRecordsPaged(
            @Parameter(description = "Car ID") @PathVariable Long carId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get maintenance records paged for car: {}", carId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("serviceDate").descending());
        Page<MaintenanceRecordDTO> records = maintenanceService.getMaintenanceRecordsByCar(carId, pageable);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update maintenance record", description = "Update maintenance record information")
    public ResponseEntity<ApiResponse<MaintenanceRecordDTO>> updateMaintenanceRecord(
            @Parameter(description = "Maintenance record ID") @PathVariable Long id,
            @Valid @RequestBody MaintenanceRecordDTO dto) {
        log.info("REST request to update maintenance record: {}", id);
        MaintenanceRecordDTO updated = maintenanceService.updateMaintenanceRecord(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Maintenance record updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete maintenance record", description = "Delete a maintenance record")
    public ResponseEntity<ApiResponse<Void>> deleteMaintenanceRecord(
            @Parameter(description = "Maintenance record ID") @PathVariable Long id) {
        log.info("REST request to delete maintenance record: {}", id);
        maintenanceService.deleteMaintenanceRecord(id);
        return ResponseEntity.ok(ApiResponse.success("Maintenance record deleted successfully", null));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming maintenance", description = "Retrieve upcoming maintenance records")
    public ResponseEntity<ApiResponse<List<MaintenanceRecordDTO>>> getUpcomingMaintenance() {
        log.info("REST request to get upcoming maintenance");
        List<MaintenanceRecordDTO> records = maintenanceService.getUpcomingMaintenance();
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/car/{carId}/history")
    @Operation(summary = "Get maintenance history", description = "Retrieve maintenance history for a period")
    public ResponseEntity<ApiResponse<List<MaintenanceRecordDTO>>> getMaintenanceHistory(
            @Parameter(description = "Car ID") @PathVariable Long carId,
            @Parameter(description = "Start date") @RequestParam String startDate,
            @Parameter(description = "End date") @RequestParam String endDate) {
        log.info("REST request to get maintenance history for car: {} from {} to {}", carId, startDate, endDate);
        List<MaintenanceRecordDTO> records = maintenanceService.getMaintenanceHistory(
                carId, LocalDate.parse(startDate), LocalDate.parse(endDate));
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/car/{carId}/total-cost")
    @Operation(summary = "Get total maintenance cost", description = "Get total maintenance cost for a car")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalMaintenanceCost(
            @Parameter(description = "Car ID") @PathVariable Long carId) {
        log.info("REST request to get total maintenance cost for car: {}", carId);
        BigDecimal cost = maintenanceService.getTotalMaintenanceCost(carId);
        return ResponseEntity.ok(ApiResponse.success(cost));
    }

    @GetMapping("/car/{carId}/monthly-costs")
    @Operation(summary = "Get monthly maintenance costs", description = "Get monthly maintenance cost breakdown")
    public ResponseEntity<ApiResponse<List<Object[]>>> getMonthlyMaintenanceCosts(
            @Parameter(description = "Car ID") @PathVariable Long carId) {
        log.info("REST request to get monthly maintenance costs for car: {}", carId);
        List<Object[]> costs = maintenanceService.getMonthlyMaintenanceCosts(carId);
        return ResponseEntity.ok(ApiResponse.success(costs));
    }

    @GetMapping("/car/{carId}/latest")
    @Operation(summary = "Get latest maintenance", description = "Get the most recent maintenance record for a car")
    public ResponseEntity<ApiResponse<MaintenanceRecordDTO>> getLatestMaintenance(
            @Parameter(description = "Car ID") @PathVariable Long carId) {
        log.info("REST request to get latest maintenance for car: {}", carId);
        MaintenanceRecordDTO record = maintenanceService.getLatestMaintenance(carId);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @GetMapping("/car/{carId}/type/{typeId}")
    @Operation(summary = "Get maintenance by type", description = "Get maintenance records by type for a car")
    public ResponseEntity<ApiResponse<List<MaintenanceRecordDTO>>> getMaintenanceByType(
            @Parameter(description = "Car ID") @PathVariable Long carId,
            @Parameter(description = "Maintenance type ID") @PathVariable Integer typeId) {
        log.info("REST request to get maintenance by type {} for car: {}", typeId, carId);
        List<MaintenanceRecordDTO> records = maintenanceService.getMaintenanceByType(carId, typeId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
