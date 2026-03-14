package com.carmaintenance.service;

import com.carmaintenance.dto.MaintenanceRecordDTO;
import com.carmaintenance.entity.Car;
import com.carmaintenance.entity.MaintenanceRecord;
import com.carmaintenance.entity.MaintenanceType;
import com.carmaintenance.exception.ResourceNotFoundException;
import com.carmaintenance.repository.CarRepository;
import com.carmaintenance.repository.MaintenanceRecordRepository;
import com.carmaintenance.repository.MaintenanceTypeRepository;
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
 * Maintenance Service
 * Business logic for maintenance records
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaintenanceService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final CarRepository carRepository;
    private final MaintenanceTypeRepository maintenanceTypeRepository;

    /**
     * Create a new maintenance record
     */
    @CacheEvict(value = {"maintenance", "carDetails"}, allEntries = true)
    public MaintenanceRecordDTO createMaintenanceRecord(MaintenanceRecordDTO dto) {
        log.info("Creating maintenance record for car ID: {}", dto.getCarId());

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + dto.getCarId()));

        MaintenanceType maintenanceType = maintenanceTypeRepository.findById(dto.getMaintenanceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance type not found with id: " + dto.getMaintenanceTypeId()));

        MaintenanceRecord record = mapToEntity(dto, car, maintenanceType);

        // Calculate next service dates if not provided
        if (record.getNextServiceMileage() == null && maintenanceType.getRecommendedIntervalKm() != null) {
            record.setNextServiceMileage(record.getMileageAtService() + maintenanceType.getRecommendedIntervalKm());
        }
        if (record.getNextServiceDate() == null && maintenanceType.getRecommendedIntervalMonths() != null) {
            record.setNextServiceDate(dto.getServiceDate().plusMonths(maintenanceType.getRecommendedIntervalMonths()));
        }

        record = maintenanceRecordRepository.save(record);
        log.info("Maintenance record created successfully with ID: {}", record.getId());

        return mapToDTO(record);
    }

    /**
     * Update maintenance record
     */
    @CacheEvict(value = {"maintenance", "carDetails"}, allEntries = true)
    public MaintenanceRecordDTO updateMaintenanceRecord(Long id, MaintenanceRecordDTO dto) {
        log.info("Updating maintenance record with ID: {}", id);

        MaintenanceRecord record = maintenanceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found with id: " + id));

        updateRecordFromDTO(record, dto);
        record = maintenanceRecordRepository.save(record);

        log.info("Maintenance record updated successfully with ID: {}", record.getId());
        return mapToDTO(record);
    }

    /**
     * Get maintenance record by ID
     */
    @Cacheable(value = "maintenance", key = "#id")
    @Transactional(readOnly = true)
    public MaintenanceRecordDTO getMaintenanceRecordById(Long id) {
        MaintenanceRecord record = maintenanceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found with id: " + id));
        return mapToDTO(record);
    }

    /**
     * Get all maintenance records for a car
     */
    @Transactional(readOnly = true)
    public List<MaintenanceRecordDTO> getMaintenanceRecordsByCar(Long carId) {
        return maintenanceRecordRepository.findByCarIdOrderByServiceDateDesc(carId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get maintenance records with pagination
     */
    @Transactional(readOnly = true)
    public Page<MaintenanceRecordDTO> getMaintenanceRecordsByCar(Long carId, Pageable pageable) {
        return maintenanceRecordRepository.findByCarIdOrderByServiceDateDesc(carId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Delete maintenance record
     */
    @CacheEvict(value = {"maintenance", "carDetails"}, allEntries = true)
    public void deleteMaintenanceRecord(Long id) {
        log.info("Deleting maintenance record with ID: {}", id);
        if (!maintenanceRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance record not found with id: " + id);
        }
        maintenanceRecordRepository.deleteById(id);
        log.info("Maintenance record deleted successfully with ID: {}", id);
    }

    /**
     * Get upcoming maintenance
     */
    @Transactional(readOnly = true)
    public List<MaintenanceRecordDTO> getUpcomingMaintenance() {
        return maintenanceRecordRepository.findUpcomingMaintenance(LocalDate.now().plusMonths(1)).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get maintenance history for a car
     */
    @Transactional(readOnly = true)
    public List<MaintenanceRecordDTO> getMaintenanceHistory(Long carId, LocalDate startDate, LocalDate endDate) {
        return maintenanceRecordRepository.findByServiceDateBetween(startDate, endDate).stream()
                .filter(m -> m.getCar().getId().equals(carId))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get total maintenance cost for a car
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalMaintenanceCost(Long carId) {
        BigDecimal cost = maintenanceRecordRepository.getTotalMaintenanceCostByCar(carId);
        return cost != null ? cost : BigDecimal.ZERO;
    }

    /**
     * Get monthly maintenance costs
     */
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyMaintenanceCosts(Long carId) {
        return maintenanceRecordRepository.getMonthlyMaintenanceCosts(carId);
    }

    /**
     * Get latest maintenance for a car
     */
    @Transactional(readOnly = true)
    public MaintenanceRecordDTO getLatestMaintenance(Long carId) {
        MaintenanceRecord record = maintenanceRecordRepository.findLatestByCar(carId);
        return record != null ? mapToDTO(record) : null;
    }

    /**
     * Get maintenance records by type
     */
    @Transactional(readOnly = true)
    public List<MaintenanceRecordDTO> getMaintenanceByType(Long carId, Integer typeId) {
        return maintenanceRecordRepository.findByCarAndType(carId, typeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Mapping methods
    private MaintenanceRecord mapToEntity(MaintenanceRecordDTO dto, Car car, MaintenanceType maintenanceType) {
        return MaintenanceRecord.builder()
                .car(car)
                .maintenanceType(maintenanceType)
                .serviceDate(dto.getServiceDate())
                .mileageAtService(dto.getMileageAtService())
                .cost(dto.getCost())
                .laborCost(dto.getLaborCost())
                .partsCost(dto.getPartsCost())
                .workshopName(dto.getWorkshopName())
                .workshopLocation(dto.getWorkshopLocation())
                .technicianName(dto.getTechnicianName())
                .description(dto.getDescription())
                .partsUsed(dto.getPartsUsed())
                .nextServiceMileage(dto.getNextServiceMileage())
                .nextServiceDate(dto.getNextServiceDate())
                .warrantyMonths(dto.getWarrantyMonths())
                .invoiceNumber(dto.getInvoiceNumber())
                .receiptImageUrl(dto.getReceiptImageUrl())
                .status(dto.getStatus() != null ? MaintenanceRecord.MaintenanceStatus.valueOf(dto.getStatus()) : MaintenanceRecord.MaintenanceStatus.completed)
                .rating(dto.getRating())
                .notes(dto.getNotes())
                .build();
    }

    private void updateRecordFromDTO(MaintenanceRecord record, MaintenanceRecordDTO dto) {
        if (dto.getServiceDate() != null) record.setServiceDate(dto.getServiceDate());
        if (dto.getMileageAtService() != null) record.setMileageAtService(dto.getMileageAtService());
        if (dto.getCost() != null) record.setCost(dto.getCost());
        if (dto.getLaborCost() != null) record.setLaborCost(dto.getLaborCost());
        if (dto.getPartsCost() != null) record.setPartsCost(dto.getPartsCost());
        if (dto.getWorkshopName() != null) record.setWorkshopName(dto.getWorkshopName());
        if (dto.getWorkshopLocation() != null) record.setWorkshopLocation(dto.getWorkshopLocation());
        if (dto.getTechnicianName() != null) record.setTechnicianName(dto.getTechnicianName());
        if (dto.getDescription() != null) record.setDescription(dto.getDescription());
        if (dto.getPartsUsed() != null) record.setPartsUsed(dto.getPartsUsed());
        if (dto.getNextServiceMileage() != null) record.setNextServiceMileage(dto.getNextServiceMileage());
        if (dto.getNextServiceDate() != null) record.setNextServiceDate(dto.getNextServiceDate());
        if (dto.getWarrantyMonths() != null) record.setWarrantyMonths(dto.getWarrantyMonths());
        if (dto.getInvoiceNumber() != null) record.setInvoiceNumber(dto.getInvoiceNumber());
        if (dto.getReceiptImageUrl() != null) record.setReceiptImageUrl(dto.getReceiptImageUrl());
        if (dto.getStatus() != null) record.setStatus(MaintenanceRecord.MaintenanceStatus.valueOf(dto.getStatus()));
        if (dto.getRating() != null) record.setRating(dto.getRating());
        if (dto.getNotes() != null) record.setNotes(dto.getNotes());
    }

    private MaintenanceRecordDTO mapToDTO(MaintenanceRecord record) {
        return MaintenanceRecordDTO.builder()
                .id(record.getId())
                .carId(record.getCar().getId())
                .maintenanceTypeId(record.getMaintenanceType().getId())
                .maintenanceTypeName(record.getMaintenanceType().getName())
                .maintenanceTypeNameAr(record.getMaintenanceType().getNameAr())
                .serviceDate(record.getServiceDate())
                .mileageAtService(record.getMileageAtService())
                .cost(record.getCost())
                .laborCost(record.getLaborCost())
                .partsCost(record.getPartsCost())
                .workshopName(record.getWorkshopName())
                .workshopLocation(record.getWorkshopLocation())
                .technicianName(record.getTechnicianName())
                .description(record.getDescription())
                .partsUsed(record.getPartsUsed())
                .nextServiceMileage(record.getNextServiceMileage())
                .nextServiceDate(record.getNextServiceDate())
                .warrantyMonths(record.getWarrantyMonths())
                .invoiceNumber(record.getInvoiceNumber())
                .receiptImageUrl(record.getReceiptImageUrl())
                .status(record.getStatus().name())
                .rating(record.getRating())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .carPlateNumber(record.getCar().getPlateNumber())
                .carBrand(record.getCar().getBrand())
                .carModel(record.getCar().getModel())
                .build();
    }
}
