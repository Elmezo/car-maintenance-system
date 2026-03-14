package com.carmaintenance.repository;

import com.carmaintenance.entity.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repair Repository
 */
@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {

    List<Repair> findByFailureId(Long failureId);

    List<Repair> findByCarIdOrderByRepairDateDesc(Long carId);

    List<Repair> findByRepairDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(r.cost) FROM Repair r WHERE r.car.id = :carId")
    BigDecimal getTotalRepairCostByCar(@Param("carId") Long carId);

    @Query("SELECT SUM(r.cost) FROM Repair r WHERE r.car.id = :carId AND r.repairDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRepairCostByCarAndPeriod(
            @Param("carId") Long carId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT AVG(r.cost) FROM Repair r")
    Double getAverageRepairCost();

    @Query("SELECT r FROM Repair r WHERE r.followUpRequired = true AND r.followUpDate <= :date")
    List<Repair> findRepairsNeedingFollowUp(@Param("date") LocalDate date);

    @Query("SELECT COUNT(r) FROM Repair r WHERE r.isSuccessful = false")
    Long countUnsuccessfulRepairs();

    @Query("SELECT r.workshopName, COUNT(r), SUM(r.cost) FROM Repair r WHERE r.workshopName IS NOT NULL GROUP BY r.workshopName ORDER BY COUNT(r) DESC")
    List<Object[]> getWorkshopStatistics();
}
