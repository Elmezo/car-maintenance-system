package com.carmaintenance.repository;

import com.carmaintenance.entity.Failure;
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
 * Failure Repository
 */
@Repository
public interface FailureRepository extends JpaRepository<Failure, Long> {

    List<Failure> findByCarIdOrderByFailureDateDesc(Long carId);

    List<Failure> findBySeverity(Failure.Severity severity);

    List<Failure> findByFailureDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT f FROM Failure f WHERE f.car.id = :carId AND f.severity IN ('major', 'critical')")
    List<Failure> findMajorFailuresByCar(@Param("carId") Long carId);

    @Query("SELECT f.failureType, COUNT(f) as count FROM Failure f GROUP BY f.failureType ORDER BY count DESC")
    List<Object[]> countByFailureType();

    @Query("SELECT f.failureType.name, COUNT(f) as count FROM Failure f " +
           "WHERE f.failureDate >= :startDate GROUP BY f.failureType.name ORDER BY count DESC")
    List<Object[]> findMostCommonFailures(@Param("startDate") LocalDate startDate);

    @Query("SELECT COUNT(f) FROM Failure f WHERE f.car.id = :carId")
    Long countByCarId(@Param("carId") Long carId);

    @Query("SELECT COUNT(f) FROM Failure f WHERE f.car.id = :carId AND f.failureDate >= :startDate")
    Long countRecentFailuresByCar(@Param("carId") Long carId, @Param("startDate") LocalDate startDate);

    @Query("SELECT f FROM Failure f WHERE f.isRecurring = true")
    List<Failure> findRecurringFailures();

    @Query("SELECT f FROM Failure f LEFT JOIN f.repairs r WHERE r IS NULL")
    List<Failure> findUnrepairedFailures();

    @Query("SELECT COUNT(f) FROM Failure f WHERE f.severity = :severity AND f.failureDate >= :startDate")
    Long countBySeverityAndDateAfter(@Param("severity") Failure.Severity severity, @Param("startDate") LocalDate startDate);

    @Query("SELECT AVG(DATEDIFF(r.repairDate, f.failureDate)) FROM Failure f JOIN f.repairs r WHERE f.car.id = :carId")
    Double getAverageRepairTime(@Param("carId") Long carId);

    @Query("SELECT f.failureType.category, COUNT(f) FROM Failure f GROUP BY f.failureType.category")
    List<Object[]> countByCategory();

    Page<Failure> findByCarIdOrderByFailureDateDesc(Long carId, Pageable pageable);
}
