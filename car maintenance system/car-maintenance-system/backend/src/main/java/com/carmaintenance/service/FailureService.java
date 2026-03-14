package com.carmaintenance.service;

import com.carmaintenance.dto.FailureDTO;
import com.carmaintenance.dto.RepairDTO;
import com.carmaintenance.entity.*;
import com.carmaintenance.exception.ResourceNotFoundException;
import com.carmaintenance.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Failure Service
 * Business logic for failure management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FailureService {

    private final FailureRepository failureRepository;
    private final CarRepository carRepository;
    private final FailureTypeRepository failureTypeRepository;
    private final RepairRepository repairRepository;

    /**
     * Create a new failure record
     */
    @CacheEvict(value = {"failures", "carDetails"}, allEntries = true)
    public FailureDTO createFailure(FailureDTO dto) {
        log.info("Creating failure record for car ID: {}", dto.getCarId());

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + dto.getCarId()));

        FailureType failureType = failureTypeRepository.findById(dto.getFailureTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Failure type not found with id: " + dto.getFailureTypeId()));

        Failure failure = mapToEntity(dto, car, failureType);

        // Check for recurring failure
        if (dto.getParentFailureId() != null) {
            Failure parentFailure = failureRepository.findById(dto.getParentFailureId())
                    .orElse(null);
            if (parentFailure != null) {
                failure.setParentFailure(parentFailure);
                failure.setIsRecurring(true);
            }
        }

        failure = failureRepository.save(failure);
        log.info("Failure record created successfully with ID: {}", failure.getId());

        return mapToDTO(failure);
    }

    /**
     * Update failure record
     */
    @CacheEvict(value = {"failures", "carDetails"}, allEntries = true)
    public FailureDTO updateFailure(Long id, FailureDTO dto) {
        log.info("Updating failure record with ID: {}", id);

        Failure failure = failureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Failure not found with id: " + id));

        updateFailureFromDTO(failure, dto);
        failure = failureRepository.save(failure);

        log.info("Failure record updated successfully with ID: {}", failure.getId());
        return mapToDTO(failure);
    }

    /**
     * Get failure by ID
     */
    @Cacheable(value = "failures", key = "#id")
    @Transactional(readOnly = true)
    public FailureDTO getFailureById(Long id) {
        Failure failure = failureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Failure not found with id: " + id));
        return mapToDTO(failure);
    }

    /**
     * Get all failures for a car
     */
    @Transactional(readOnly = true)
    public List<FailureDTO> getFailuresByCar(Long carId) {
        return failureRepository.findByCarIdOrderByFailureDateDesc(carId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get failures with pagination
     */
    @Transactional(readOnly = true)
    public Page<FailureDTO> getFailuresByCar(Long carId, Pageable pageable) {
        return failureRepository.findByCarIdOrderByFailureDateDesc(carId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Get major failures for a car
     */
    @Transactional(readOnly = true)
    public List<FailureDTO> getMajorFailures(Long carId) {
        return failureRepository.findMajorFailuresByCar(carId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get most common failures
     */
    @Transactional(readOnly = true)
    public List<Object[]> getMostCommonFailures(int months) {
        LocalDate startDate = LocalDate.now().minusMonths(months);
        return failureRepository.findMostCommonFailures(startDate);
    }

    /**
     * Delete failure record
     */
    @CacheEvict(value = {"failures", "carDetails"}, allEntries = true)
    public void deleteFailure(Long id) {
        log.info("Deleting failure record with ID: {}", id);
        if (!failureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Failure not found with id: " + id);
        }
        failureRepository.deleteById(id);
        log.info("Failure record deleted successfully with ID: {}", id);
    }

    /**
     * Get unrepaired failures
     */
    @Transactional(readOnly = true)
    public List<FailureDTO> getUnrepairedFailures() {
        return failureRepository.findUnrepairedFailures().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recurring failures
     */
    @Transactional(readOnly = true)
    public List<FailureDTO> getRecurringFailures() {
        return failureRepository.findRecurringFailures().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Add repair to failure
     */
    @CacheEvict(value = {"failures", "carDetails"}, allEntries = true)
    public RepairDTO addRepair(Long failureId, RepairDTO dto) {
        log.info("Adding repair for failure ID: {}", failureId);

        Failure failure = failureRepository.findById(failureId)
                .orElseThrow(() -> new ResourceNotFoundException("Failure not found with id: " + failureId));

        Repair repair = Repair.builder()
                .failure(failure)
                .car(failure.getCar())
                .repairDate(dto.getRepairDate())
                .mileageAtRepair(dto.getMileageAtRepair())
                .cost(dto.getCost())
                .laborCost(dto.getLaborCost())
                .partsCost(dto.getPartsCost())
                .workshopName(dto.getWorkshopName())
                .technicianName(dto.getTechnicianName())
                .description(dto.getDescription())
                .partsReplaced(dto.getPartsReplaced())
                .repairMethod(dto.getRepairMethod())
                .warrantyMonths(dto.getWarrantyMonths())
                .invoiceNumber(dto.getInvoiceNumber())
                .status(dto.getStatus() != null ? Repair.RepairStatus.valueOf(dto.getStatus()) : Repair.RepairStatus.completed)
                .isSuccessful(dto.getIsSuccessful() != null ? dto.getIsSuccessful() : true)
                .followUpRequired(dto.getFollowUpRequired())
                .followUpDate(dto.getFollowUpDate())
                .notes(dto.getNotes())
                .build();

        repair = repairRepository.save(repair);
        log.info("Repair added successfully with ID: {}", repair.getId());

        return mapRepairToDTO(repair);
    }

    /**
     * Get repairs for a failure
     */
    @Transactional(readOnly = true)
    public List<RepairDTO> getRepairsForFailure(Long failureId) {
        return repairRepository.findByFailureId(failureId).stream()
                .map(this::mapRepairToDTO)
                .collect(Collectors.toList());
    }

    // Mapping methods
    private Failure mapToEntity(FailureDTO dto, Car car, FailureType failureType) {
        return Failure.builder()
                .car(car)
                .failureType(failureType)
                .failureDate(dto.getFailureDate())
                .mileageAtFailure(dto.getMileageAtFailure())
                .severity(dto.getSeverity() != null ? Failure.Severity.valueOf(dto.getSeverity()) : Failure.Severity.moderate)
                .description(dto.getDescription())
                .symptoms(dto.getSymptoms())
                .rootCause(dto.getRootCause())
                .weatherConditions(dto.getWeatherConditions())
                .drivingConditions(dto.getDrivingConditions())
                .isRecurring(dto.getIsRecurring() != null ? dto.getIsRecurring() : false)
                .build();
    }

    private void updateFailureFromDTO(Failure failure, FailureDTO dto) {
        if (dto.getFailureDate() != null) failure.setFailureDate(dto.getFailureDate());
        if (dto.getMileageAtFailure() != null) failure.setMileageAtFailure(dto.getMileageAtFailure());
        if (dto.getSeverity() != null) failure.setSeverity(Failure.Severity.valueOf(dto.getSeverity()));
        if (dto.getDescription() != null) failure.setDescription(dto.getDescription());
        if (dto.getSymptoms() != null) failure.setSymptoms(dto.getSymptoms());
        if (dto.getRootCause() != null) failure.setRootCause(dto.getRootCause());
        if (dto.getWeatherConditions() != null) failure.setWeatherConditions(dto.getWeatherConditions());
        if (dto.getDrivingConditions() != null) failure.setDrivingConditions(dto.getDrivingConditions());
        if (dto.getIsRecurring() != null) failure.setIsRecurring(dto.getIsRecurring());
    }

    private FailureDTO mapToDTO(Failure failure) {
        int repairCount = failure.getRepairs() != null ? failure.getRepairs().size() : 0;

        return FailureDTO.builder()
                .id(failure.getId())
                .carId(failure.getCar().getId())
                .failureTypeId(failure.getFailureType().getId())
                .failureTypeName(failure.getFailureType().getName())
                .failureTypeNameAr(failure.getFailureType().getNameAr())
                .failureCategory(failure.getFailureType().getCategory())
                .failureDate(failure.getFailureDate())
                .mileageAtFailure(failure.getMileageAtFailure())
                .severity(failure.getSeverity().name())
                .description(failure.getDescription())
                .symptoms(failure.getSymptoms())
                .rootCause(failure.getRootCause())
                .weatherConditions(failure.getWeatherConditions())
                .drivingConditions(failure.getDrivingConditions())
                .isRecurring(failure.getIsRecurring())
                .parentFailureId(failure.getParentFailure() != null ? failure.getParentFailure().getId() : null)
                .createdAt(failure.getCreatedAt())
                .updatedAt(failure.getUpdatedAt())
                .carPlateNumber(failure.getCar().getPlateNumber())
                .carBrand(failure.getCar().getBrand())
                .carModel(failure.getCar().getModel())
                .isRepaired(failure.isRepaired())
                .repairCount(repairCount)
                .build();
    }

    private RepairDTO mapRepairToDTO(Repair repair) {
        return RepairDTO.builder()
                .id(repair.getId())
                .failureId(repair.getFailure().getId())
                .carId(repair.getCar().getId())
                .repairDate(repair.getRepairDate())
                .mileageAtRepair(repair.getMileageAtRepair())
                .cost(repair.getCost())
                .laborCost(repair.getLaborCost())
                .partsCost(repair.getPartsCost())
                .workshopName(repair.getWorkshopName())
                .technicianName(repair.getTechnicianName())
                .description(repair.getDescription())
                .partsReplaced(repair.getPartsReplaced())
                .repairMethod(repair.getRepairMethod())
                .warrantyMonths(repair.getWarrantyMonths())
                .invoiceNumber(repair.getInvoiceNumber())
                .status(repair.getStatus().name())
                .isSuccessful(repair.getIsSuccessful())
                .followUpRequired(repair.getFollowUpRequired())
                .followUpDate(repair.getFollowUpDate())
                .notes(repair.getNotes())
                .createdAt(repair.getCreatedAt())
                .updatedAt(repair.getUpdatedAt())
                .failureTypeName(repair.getFailure().getFailureType().getName())
                .failureSeverity(repair.getFailure().getSeverity().name())
                .build();
    }
}
