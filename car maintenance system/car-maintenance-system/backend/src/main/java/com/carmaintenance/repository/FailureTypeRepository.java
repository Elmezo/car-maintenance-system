package com.carmaintenance.repository;

import com.carmaintenance.entity.FailureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Failure Type Repository
 */
@Repository
public interface FailureTypeRepository extends JpaRepository<FailureType, Integer> {

    List<FailureType> findByCategory(String category);

    @Query("SELECT ft FROM FailureType ft WHERE ft.name LIKE %:keyword% OR ft.nameAr LIKE %:keyword%")
    List<FailureType> searchByName(@Param("keyword") String keyword);
}
