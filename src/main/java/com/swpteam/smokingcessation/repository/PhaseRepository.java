package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Phase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, String> {

    List<Phase> findAllByPlanId(String planId);

    Optional<Phase> findByIdAndIsDeletedFalse(String id);

    @Query("SELECT p FROM Phase p WHERE p.plan.id = :planId AND " +
            "(:startDate <= p.endDate AND :endDate >= p.startDate)")
    List<Phase> findOverlappingPhases(@Param("planId") String planId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    List<Phase> findAllByPlanIdAndIsDeletedFalseOrderByStartDateAsc(String planId);

    List<Phase> findByPlanIdAndIsDeletedFalse(String planId);

}