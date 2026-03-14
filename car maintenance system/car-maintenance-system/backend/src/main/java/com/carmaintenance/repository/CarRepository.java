package com.carmaintenance.repository;

import com.carmaintenance.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Car Repository
 * Database operations for Car entity
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByPlateNumber(String plateNumber);

    Optional<Car> findByVin(String vin);

    List<Car> findByStatus(Car.CarStatus status);

    List<Car> findByBrandAndModel(String brand, String model);

    List<Car> findByOwnerNameContainingIgnoreCase(String ownerName);

    @Query("SELECT c FROM Car c WHERE c.currentMileage >= :minMileage AND c.currentMileage <= :maxMileage")
    List<Car> findByMileageRange(@Param("minMileage") Integer minMileage, @Param("maxMileage") Integer maxMileage);

    @Query("SELECT c FROM Car c WHERE c.year >= :startYear AND c.year <= :endYear")
    List<Car> findByYearRange(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);

    @Query("SELECT c FROM Car c WHERE c.warrantyExpiry < :date AND c.status = 'active'")
    List<Car> findCarsWithExpiredWarranty(@Param("date") LocalDate date);

    @Query("SELECT DISTINCT c.brand FROM Car c ORDER BY c.brand")
    List<String> findAllBrands();

    @Query("SELECT DISTINCT c.model FROM Car c WHERE c.brand = :brand ORDER BY c.model")
    List<String> findModelsByBrand(@Param("brand") String brand);

    @Query("SELECT COUNT(c) FROM Car c WHERE c.status = :status")
    Long countByStatus(@Param("status") Car.CarStatus status);

    @Query("SELECT AVG(c.currentMileage) FROM Car c WHERE c.status = 'active'")
    Double getAverageMileage();

    @Query("SELECT c.engineType, COUNT(c) FROM Car c GROUP BY c.engineType")
    List<Object[]> countByEngineType();

    Page<Car> findByStatusOrderByCreatedAtDesc(Car.CarStatus status, Pageable pageable);

    boolean existsByPlateNumber(String plateNumber);

    boolean existsByVin(String vin);
}
