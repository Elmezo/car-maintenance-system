package com.carmaintenance.controller;

import com.carmaintenance.dto.ApiResponse;
import com.carmaintenance.dto.CarDTO;
import com.carmaintenance.service.CarService;
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
 * Car Controller
 * REST API endpoints for car management
 */
@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Car Management", description = "APIs for managing cars")
@CrossOrigin(origins = "*")
public class CarController {

    private final CarService carService;

    @PostMapping
    @Operation(summary = "Create a new car", description = "Add a new car to the system")
    public ResponseEntity<ApiResponse<CarDTO>> createCar(@Valid @RequestBody CarDTO carDTO) {
        log.info("REST request to create car: {}", carDTO.getPlateNumber());
        CarDTO createdCar = carService.createCar(carDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Car created successfully", createdCar));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get car by ID", description = "Retrieve car details by ID")
    public ResponseEntity<ApiResponse<CarDTO>> getCarById(
            @Parameter(description = "Car ID") @PathVariable Long id) {
        log.info("REST request to get car by ID: {}", id);
        CarDTO car = carService.getCarById(id);
        return ResponseEntity.ok(ApiResponse.success(car));
    }

    @GetMapping("/plate/{plateNumber}")
    @Operation(summary = "Get car by plate number", description = "Retrieve car details by plate number")
    public ResponseEntity<ApiResponse<CarDTO>> getCarByPlateNumber(
            @Parameter(description = "Plate number") @PathVariable String plateNumber) {
        log.info("REST request to get car by plate number: {}", plateNumber);
        CarDTO car = carService.getCarByPlateNumber(plateNumber);
        return ResponseEntity.ok(ApiResponse.success(car));
    }

    @GetMapping
    @Operation(summary = "Get all cars", description = "Retrieve all cars in the system")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getAllCars() {
        log.info("REST request to get all cars");
        List<CarDTO> cars = carService.getAllCars();
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active cars", description = "Retrieve all active cars")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getActiveCars() {
        log.info("REST request to get active cars");
        List<CarDTO> cars = carService.getActiveCars();
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @GetMapping("/paged")
    @Operation(summary = "Get cars with pagination", description = "Retrieve cars with pagination support")
    public ResponseEntity<ApiResponse<Page<CarDTO>>> getCarsPaged(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get cars paged: page={}, size={}", page, size);
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CarDTO> cars = carService.getCars(pageable);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update car", description = "Update car information")
    public ResponseEntity<ApiResponse<CarDTO>> updateCar(
            @Parameter(description = "Car ID") @PathVariable Long id,
            @Valid @RequestBody CarDTO carDTO) {
        log.info("REST request to update car: {}", id);
        CarDTO updatedCar = carService.updateCar(id, carDTO);
        return ResponseEntity.ok(ApiResponse.success("Car updated successfully", updatedCar));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete car", description = "Delete a car from the system")
    public ResponseEntity<ApiResponse<Void>> deleteCar(
            @Parameter(description = "Car ID") @PathVariable Long id) {
        log.info("REST request to delete car: {}", id);
        carService.deleteCar(id);
        return ResponseEntity.ok(ApiResponse.success("Car deleted successfully", null));
    }

    @PatchMapping("/{id}/mileage")
    @Operation(summary = "Update car mileage", description = "Update the current mileage of a car")
    public ResponseEntity<ApiResponse<CarDTO>> updateMileage(
            @Parameter(description = "Car ID") @PathVariable Long id,
            @Parameter(description = "New mileage") @RequestParam Integer mileage) {
        log.info("REST request to update mileage for car {}: {}", id, mileage);
        CarDTO car = carService.updateMileage(id, mileage);
        return ResponseEntity.ok(ApiResponse.success("Mileage updated successfully", car));
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "Get cars by brand", description = "Retrieve cars by brand")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getCarsByBrand(
            @Parameter(description = "Brand name") @PathVariable String brand) {
        log.info("REST request to get cars by brand: {}", brand);
        List<CarDTO> cars = carService.getCarsByBrand(brand);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @GetMapping("/warranty-expired")
    @Operation(summary = "Get cars with expired warranty", description = "Retrieve cars with expired warranty")
    public ResponseEntity<ApiResponse<List<CarDTO>>> getCarsWithExpiredWarranty() {
        log.info("REST request to get cars with expired warranty");
        List<CarDTO> cars = carService.getCarsWithExpiredWarranty();
        return ResponseEntity.ok(ApiResponse.success(cars));
    }

    @GetMapping("/search")
    @Operation(summary = "Search cars", description = "Search cars by keyword")
    public ResponseEntity<ApiResponse<List<CarDTO>>> searchCars(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        log.info("REST request to search cars: {}", keyword);
        List<CarDTO> cars = carService.searchCars(keyword);
        return ResponseEntity.ok(ApiResponse.success(cars));
    }
}
