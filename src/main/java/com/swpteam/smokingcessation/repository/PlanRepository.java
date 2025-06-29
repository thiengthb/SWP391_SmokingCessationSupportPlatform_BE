package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, String> {

    Page<Plan> findAllByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Optional<Plan> findTopByAccountIdAndIsDeletedFalseOrderByCreatedAtDesc(String accountId);

    Optional<Plan> findByIdAndIsDeletedFalse(String id);

    List<Plan> findAllByPlanStatusAndIsDeletedFalse(PlanStatus planStatus);

    Optional<Plan> findByAccountIdAndPlanStatusAndIsDeletedFalse(String accountId, PlanStatus planStatus);


}
