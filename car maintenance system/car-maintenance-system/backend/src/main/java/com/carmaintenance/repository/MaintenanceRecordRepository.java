package com.carmaintenance.repository;

import com.carmaintenance.entity.MaintenanceRecord;
import com.carmaintenance.entity.MaintenanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Maintenance Record Repository
 */
@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    List<MaintenanceRecord> findByCarIdOrderByServiceDateDesc(Long carId);

    List<MaintenanceRecord> findByCarIdAndStatus(Long carId, MaintenanceRecord.MaintenanceStatus status);

    List<MaintenanceRecord> findByServiceDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.car.id = :carId AND mr.maintenanceType.id = :typeId ORDER BY mr.serviceDate DESC")
    List<MaintenanceRecord> findByCarAndType(@Param("carId") Long carId, @Param("typeId") Integer typeId);

    @Query("SELECT SUM(mr.cost) FROM MaintenanceRecord mr WHERE mr.car.id = :carId")
    BigDecimal getTotalMaintenanceCostByCar(@Param("carId") Long carId);

    @Query("SELECT SUM(mr.cost) FROM MaintenanceRecord mr WHERE mr.car.id = :carId AND mr.serviceDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalMaintenanceCostByCarAndPeriod(
            @Param("carId") Long carId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(mr) FROM MaintenanceRecord mr WHERE mr.car.id = :carId")
    Long countByCarId(@Param("carId") Long carId);

    @Query("SELECT mr.maintenanceType, COUNT(mr) as count FROM MaintenanceRecord mr GROUP BY mr.maintenanceType ORDER BY count DESC")
    List<Object[]> countByMaintenanceType();

    @Query("SELECT AVG(mr.cost) FROM MaintenanceRecord mr WHERE mr.maintenanceType.id = :typeId")
    Double getAverageCostByType(@Param("typeId") Integer typeId);

    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.nextServiceDate <= :date AND mr.status = 'completed'")
    List<MaintenanceRecord> findUpcomingMaintenance(@Param("date") LocalDate date);

    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.nextServiceMileage <= :mileage AND mr.car.id = :carId AND mr.status = 'completed'")
    List<MaintenanceRecord> findMaintenanceDueByMileage(@Param("carId") Long carId, @Param("mileage") Integer mileage);

    @Query("SELECT YEAR(mr.serviceDate), MONTH(mr.serviceDate), SUM(mr.cost) FROM MaintenanceRecord mr " +
           "WHERE mr.car.id = :carId GROUP BY YEAR(mr.serviceDate), MONTH(mr.serviceDate) " +
           "ORDER BY YEAR(mr.serviceDate) DESC, MONTH(mr.serviceDate) DESC")
    List<Object[]> getMonthlyMaintenanceCosts(@Param("carId") Long carId);

    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.car.id = :carId ORDER BY mr.serviceDate DESC LIMIT 1")
    MaintenanceRecord findLatestByCar(@Param("carId") Long carId);

    Page<MaintenanceRecord> findByCarIdOrderByServiceDateDesc(Long carId, Pageable pageable);

    @Query("SELECT SUM(mr.cost) FROM MaintenanceRecord mr WHERE mr.serviceDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalCostByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
