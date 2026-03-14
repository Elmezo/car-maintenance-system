package com.carmaintenance.repository;

import com.carmaintenance.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Prediction Repository
 */
@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    List<Prediction> findByCarIdOrderByPredictionDateDesc(Long carId);

    List<Prediction> findByPredictionType(Prediction.PredictionType predictionType);

    @Query("SELECT p FROM Prediction p WHERE p.car.id = :carId AND p.predictedDate BETWEEN :startDate AND :endDate")
    List<Prediction> findByCarAndDateRange(
            @Param("carId") Long carId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Prediction p WHERE p.car.id = :carId AND p.isAccurate IS NULL ORDER BY p.predictedDate ASC")
    List<Prediction> findUnverifiedPredictions(@Param("carId") Long carId);

    @Query("SELECT COUNT(p), SUM(CASE WHEN p.isAccurate = true THEN 1 ELSE 0 END) FROM Prediction p WHERE p.isAccurate IS NOT NULL")
    Object[] getPredictionAccuracyStats();

    @Query("SELECT p FROM Prediction p WHERE p.predictedDate <= :date AND p.isAccurate IS NULL")
    List<Prediction> findDuePredictions(@Param("date") LocalDate date);

    @Query("SELECT p.confidenceLevel, COUNT(p) FROM Prediction p GROUP BY p.confidenceLevel")
    List<Object[]> countByConfidenceLevel();
}
