package com.carmaintenance.controller;

import com.carmaintenance.dto.ApiResponse;
import com.carmaintenance.dto.FailureDTO;
import com.carmaintenance.dto.RepairDTO;
import com.carmaintenance.service.FailureService;
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

import java.util.List;

/**
 * Failure Controller
 * REST API endpoints for failure management
 */
@RestController
@RequestMapping("/failures")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Failure Management", description = "APIs for managing failures and repairs")
@CrossOrigin(origins = "*")
public class FailureController {

    private final FailureService failureService;

    @PostMapping
    @Operation(summary = "Create failure record", description = "Add a new failure record")
    public ResponseEntity<ApiResponse<FailureDTO>> createFailure(@Valid @RequestBody FailureDTO dto) {
        log.info("REST request to create failure record for car: {}", dto.getCarId());
        FailureDTO created = failureService.createFailure(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Failure record created successfully", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get failure by ID", description = "Retrieve failure record details")
    public ResponseEntity<ApiResponse<FailureDTO>> getFailureById(
            @Parameter(description = "Failure ID") @PathVariable Long id) {
        log.info("REST request to get failure: {}", id);
        FailureDTO failure = failureService.getFailureById(id);
        return ResponseEntity.ok(ApiResponse.success(failure));
    }

    @GetMapping("/car/{carId}")
    @Operation(summary = "Get failures by car", description = "Retrieve all failures for a car")
    public ResponseEntity<ApiResponse<List<FailureDTO>>> getFailuresByCar(
            @Parameter(description = "Car ID") @PathVariable Long carId) {
        log.info("REST request to get failures for car: {}", carId);
        List<FailureDTO> failures = failureService.getFailuresByCar(carId);
        return ResponseEntity.ok(ApiResponse.success(failures));
    }

    @GetMapping("/car/{carId}/paged")
    @Operation(summary = "Get failures with pagination", description = "Retrieve failures with pagination")
    public ResponseEntity<ApiResponse<Page<FailureDTO>>> getFailuresPaged(
            @Parameter(description = "Car ID") @PathVariable Long carId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get failures paged for car: {}", carId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("failureDate").descending());
        Page<FailureDTO> failures = failureService.getFailuresByCar(carId, pageable);
        return ResponseEntity.ok(ApiResponse.success(failures));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update failure record", description = "Update failure record information")
    public ResponseEntity<ApiResponse<FailureDTO>> updateFailure(
            @Parameter(description = "Failure ID") @PathVariable Long id,
            @Valid @RequestBody FailureDTO dto) {
        log.info("REST request to update failure: {}", id);
        FailureDTO updated = failureService.updateFailure(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Failure record updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete failure record", description = "Delete a failure record")
    public ResponseEntity<ApiResponse<Void>> deleteFailure(
            @Parameter(description = "Failure ID") @PathVariable Long id) {
        log.info("REST request to delete failure: {}", id);
        failureService.deleteFailure(id);
        return ResponseEntity.ok(ApiResponse.success("Failure record deleted successfully", null));
    }

    @GetMapping("/car/{carId}/major")
    @Operation(summary = "Get major failures", description = "Retrieve major and critical failures for a car")
    public ResponseEntity<ApiResponse<List<FailureDTO>>> getMajorFailures(
            @Parameter(description = "Car ID") @PathVariable Long carId) {
        log.info("REST request to get major failures for car: {}", carId);
        List<FailureDTO> failures = failureService.getMajorFailures(carId);
        return ResponseEntity.ok(ApiResponse.success(failures));
    }

    @GetMapping("/most-common")
    @Operation(summary = "Get most common failures", description = "Retrieve most common failure types")
    public ResponseEntity<ApiResponse<List<Object[]>>> getMostCommonFailures(
            @Parameter(description = "Number of months") @RequestParam(defaultValue = "12") int months) {
        log.info("REST request to get most common failures for last {} months", months);
        List<Object[]> failures = failureService.getMostCommonFailures(months);
        return ResponseEntity.ok(ApiResponse.success(failures));
    }

    @GetMapping("/unrepaired")
    @Operation(summary = "Get unrepaired failures", description = "Retrieve all unrepaired failures")
    public ResponseEntity<ApiResponse<List<FailureDTO>>> getUnrepairedFailures() {
        log.info("REST request to get unrepaired failures");
        List<FailureDTO> failures = failureService.getUnrepairedFailures();
        return ResponseEntity.ok(ApiResponse.success(failures));
    }

    @GetMapping("/recurring")
    @Operation(summary = "Get recurring failures", description = "Retrieve all recurring failures")
    public ResponseEntity<ApiResponse<List<FailureDTO>>> getRecurringFailures() {
        log.info("REST request to get recurring failures");
        List<FailureDTO> failures = failureService.getRecurringFailures();
        return ResponseEntity.ok(ApiResponse.success(failures));
    }

    // Repair endpoints
    @PostMapping("/{failureId}/repairs")
    @Operation(summary = "Add repair to failure", description = "Add a repair record to a failure")
    public ResponseEntity<ApiResponse<RepairDTO>> addRepair(
            @Parameter(description = "Failure ID") @PathVariable Long failureId,
            @Valid @RequestBody RepairDTO dto) {
        log.info("REST request to add repair for failure: {}", failureId);
        RepairDTO created = failureService.addRepair(failureId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Repair added successfully", created));
    }

    @GetMapping("/{failureId}/repairs")
    @Operation(summary = "Get repairs for failure", description = "Retrieve all repairs for a failure")
    public ResponseEntity<ApiResponse<List<RepairDTO>>> getRepairsForFailure(
            @Parameter(description = "Failure ID") @PathVariable Long failureId) {
        log.info("REST request to get repairs for failure: {}", failureId);
        List<RepairDTO> repairs = failureService.getRepairsForFailure(failureId);
        return ResponseEntity.ok(ApiResponse.success(repairs));
    }
}
