package com.carmaintenance.service;

import com.carmaintenance.dto.CarDTO;
import com.carmaintenance.entity.Car;
import com.carmaintenance.exception.ResourceNotFoundException;
import com.carmaintenance.repository.CarRepository;
import com.carmaintenance.repository.MaintenanceRecordRepository;
import com.carmaintenance.repository.FailureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Car Service
 * Business logic for car management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final FailureRepository failureRepository;

    /**
     * Create a new car
     */
    @CacheEvict(value = "cars", allEntries = true)
    public CarDTO createCar(CarDTO carDTO) {
        log.info("Creating new car with plate number: {}", carDTO.getPlateNumber());

        // Check if plate number already exists
        if (carRepository.existsByPlateNumber(carDTO.getPlateNumber())) {
            throw new IllegalArgumentException("Car with plate number " + carDTO.getPlateNumber() + " already exists");
        }

        // Check VIN if provided
        if (carDTO.getVin() != null && !carDTO.getVin().isEmpty() && carRepository.existsByVin(carDTO.getVin())) {
            throw new IllegalArgumentException("Car with VIN " + carDTO.getVin() + " already exists");
        }

        Car car = mapToEntity(carDTO);
        car = carRepository.save(car);

        log.info("Car created successfully with ID: {}", car.getId());
        return mapToDTO(car);
    }

    /**
     * Update an existing car
     */
    @CacheEvict(value = {"cars", "carDetails"}, allEntries = true)
    public CarDTO updateCar(Long id, CarDTO carDTO) {
        log.info("Updating car with ID: {}", id);

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

        // Check plate number uniqueness
        if (!car.getPlateNumber().equals(carDTO.getPlateNumber()) &&
                carRepository.existsByPlateNumber(carDTO.getPlateNumber())) {
            throw new IllegalArgumentException("Car with plate number " + carDTO.getPlateNumber() + " already exists");
        }

        updateCarFromDTO(car, carDTO);
        car = carRepository.save(car);

        log.info("Car updated successfully with ID: {}", car.getId());
        return mapToDTO(car);
    }

    /**
     * Get car by ID
     */
    @Cacheable(value = "carDetails", key = "#id")
    @Transactional(readOnly = true)
    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));
        return mapToDTOWithStats(car);
    }

    /**
     * Get car by plate number
     */
    @Transactional(readOnly = true)
    public CarDTO getCarByPlateNumber(String plateNumber) {
        Car car = carRepository.findByPlateNumber(plateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with plate number: " + plateNumber));
        return mapToDTOWithStats(car);
    }

    /**
     * Get all cars
     */
    @Cacheable(value = "cars")
    @Transactional(readOnly = true)
    public List<CarDTO> getAllCars() {
        return carRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active cars
     */
    @Transactional(readOnly = true)
    public List<CarDTO> getActiveCars() {
        return carRepository.findByStatus(Car.CarStatus.active).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get cars with pagination
     */
    @Transactional(readOnly = true)
    public Page<CarDTO> getCars(Pageable pageable) {
        return carRepository.findByStatusOrderByCreatedAtDesc(Car.CarStatus.active, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Delete a car
     */
    @CacheEvict(value = {"cars", "carDetails"}, allEntries = true)
    public void deleteCar(Long id) {
        log.info("Deleting car with ID: {}", id);
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException("Car not found with id: " + id);
        }
        carRepository.deleteById(id);
        log.info("Car deleted successfully with ID: {}", id);
    }

    /**
     * Update car mileage
     */
    @CacheEvict(value = "carDetails", key = "#id")
    public CarDTO updateMileage(Long id, Integer newMileage) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

        if (newMileage < car.getCurrentMileage()) {
            throw new IllegalArgumentException("New mileage cannot be less than current mileage");
        }

        car.setCurrentMileage(newMileage);
        car = carRepository.save(car);
        return mapToDTO(car);
    }

    /**
     * Get cars by brand
     */
    @Transactional(readOnly = true)
    public List<CarDTO> getCarsByBrand(String brand) {
        return carRepository.findByBrandAndModel(brand, null).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get cars with expired warranty
     */
    @Transactional(readOnly = true)
    public List<CarDTO> getCarsWithExpiredWarranty() {
        return carRepository.findCarsWithExpiredWarranty(LocalDate.now()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search cars
     */
    @Transactional(readOnly = true)
    public List<CarDTO> searchCars(String keyword) {
        return carRepository.findAll().stream()
                .filter(car -> car.getPlateNumber().toLowerCase().contains(keyword.toLowerCase()) ||
                        car.getBrand().toLowerCase().contains(keyword.toLowerCase()) ||
                        car.getModel().toLowerCase().contains(keyword.toLowerCase()) ||
                        (car.getOwnerName() != null && car.getOwnerName().toLowerCase().contains(keyword.toLowerCase())))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Mapping methods
    private Car mapToEntity(CarDTO dto) {
        return Car.builder()
                .plateNumber(dto.getPlateNumber())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .year(dto.getYear())
                .color(dto.getColor())
                .vin(dto.getVin())
                .currentMileage(dto.getCurrentMileage() != null ? dto.getCurrentMileage() : 0)
                .engineType(dto.getEngineType() != null ? Car.EngineType.valueOf(dto.getEngineType()) : Car.EngineType.petrol)
                .transmission(dto.getTransmission() != null ? Car.Transmission.valueOf(dto.getTransmission()) : Car.Transmission.automatic)
                .fuelCapacity(dto.getFuelCapacity())
                .ownerName(dto.getOwnerName())
                .ownerPhone(dto.getOwnerPhone())
                .ownerEmail(dto.getOwnerEmail())
                .purchaseDate(dto.getPurchaseDate())
                .warrantyExpiry(dto.getWarrantyExpiry())
                .imageUrl(dto.getImageUrl())
                .status(dto.getStatus() != null ? Car.CarStatus.valueOf(dto.getStatus()) : Car.CarStatus.active)
                .notes(dto.getNotes())
                .build();
    }

    private void updateCarFromDTO(Car car, CarDTO dto) {
        car.setPlateNumber(dto.getPlateNumber());
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setColor(dto.getColor());
        if (dto.getVin() != null) car.setVin(dto.getVin());
        if (dto.getCurrentMileage() != null) car.setCurrentMileage(dto.getCurrentMileage());
        if (dto.getEngineType() != null) car.setEngineType(Car.EngineType.valueOf(dto.getEngineType()));
        if (dto.getTransmission() != null) car.setTransmission(Car.Transmission.valueOf(dto.getTransmission()));
        car.setFuelCapacity(dto.getFuelCapacity());
        car.setOwnerName(dto.getOwnerName());
        car.setOwnerPhone(dto.getOwnerPhone());
        car.setOwnerEmail(dto.getOwnerEmail());
        car.setPurchaseDate(dto.getPurchaseDate());
        car.setWarrantyExpiry(dto.getWarrantyExpiry());
        car.setImageUrl(dto.getImageUrl());
        if (dto.getStatus() != null) car.setStatus(Car.CarStatus.valueOf(dto.getStatus()));
        car.setNotes(dto.getNotes());
    }

    private CarDTO mapToDTO(Car car) {
        return CarDTO.builder()
                .id(car.getId())
                .plateNumber(car.getPlateNumber())
                .brand(car.getBrand())
                .model(car.getModel())
                .year(car.getYear())
                .color(car.getColor())
                .vin(car.getVin())
                .currentMileage(car.getCurrentMileage())
                .engineType(car.getEngineType().name())
                .transmission(car.getTransmission().name())
                .fuelCapacity(car.getFuelCapacity())
                .ownerName(car.getOwnerName())
                .ownerPhone(car.getOwnerPhone())
                .ownerEmail(car.getOwnerEmail())
                .purchaseDate(car.getPurchaseDate())
                .warrantyExpiry(car.getWarrantyExpiry())
                .imageUrl(car.getImageUrl())
                .status(car.getStatus().name())
                .notes(car.getNotes())
                .createdAt(car.getCreatedAt())
                .updatedAt(car.getUpdatedAt())
                .carAge(car.getCarAge())
                .warrantyActive(car.isWarrantyActive())
                .build();
    }

    private CarDTO mapToDTOWithStats(Car car) {
        CarDTO dto = mapToDTO(car);

        // Add statistics
        dto.setMaintenanceCount(maintenanceRecordRepository.countByCarId(car.getId()));
        dto.setFailureCount(failureRepository.countByCarId(car.getId()));
        dto.setTotalMaintenanceCost(maintenanceRecordRepository.getTotalMaintenanceCostByCar(car.getId()));
        dto.setTotalRepairCost(
                failureRepository.findById(car.getId())
                        .map(f -> BigDecimal.ZERO)
                        .orElse(BigDecimal.ZERO)
        );

        // Calculate health score
        dto.setHealthScore(calculateHealthScore(car));

        return dto;
    }

    private int calculateHealthScore(Car car) {
        int score = 100;

        // Deduct for overdue maintenance
        long overdueMaintenance = maintenanceRecordRepository.findUpcomingMaintenance(LocalDate.now()).stream()
                .filter(m -> m.getCar().getId().equals(car.getId()))
                .count();
        score -= (int) (overdueMaintenance * 5);

        // Deduct for recent failures
        long recentFailures = failureRepository.countRecentFailuresByCar(car.getId(), LocalDate.now().minusMonths(6));
        score -= (int) (recentFailures * 3);

        // Deduct for car age
        score -= Math.min(car.getCarAge() * 2, 20);

        // Deduct for high mileage
        if (car.getCurrentMileage() > 100000) {
            score -= Math.min((car.getCurrentMileage() - 100000) / 10000, 15);
        }

        return Math.max(score, 0);
    }
}
