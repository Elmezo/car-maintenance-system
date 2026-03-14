package com.carmaintenance.repository;

import com.carmaintenance.entity.MaintenanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Maintenance Type Repository
 */
@Repository
public interface MaintenanceTypeRepository extends JpaRepository<MaintenanceType, Integer> {

    List<MaintenanceType> findByIsActiveTrue();

    List<MaintenanceType> findByCategory(MaintenanceType.MaintenanceCategory category);

    List<MaintenanceType> findByPriority(MaintenanceType.Priority priority);

    @Query("SELECT mt FROM MaintenanceType mt WHERE mt.recommendedIntervalKm IS NOT NULL OR mt.recommendedIntervalMonths IS NOT NULL")
    List<MaintenanceType> findScheduledMaintenanceTypes();

    @Query("SELECT mt FROM MaintenanceType mt WHERE mt.recommendedIntervalKm <= :km OR mt.recommendedIntervalMonths <= :months")
    List<MaintenanceType> findTypesDueWithin(@Param("km") Integer km, @Param("months") Integer months);
}
