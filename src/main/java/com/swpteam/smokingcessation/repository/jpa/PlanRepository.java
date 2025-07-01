package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.Health;
import com.swpteam.smokingcessation.domain.entity.Plan;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, String> {

    Page<Plan> findAllByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Optional<Plan> findTopByAccountIdAndIsDeletedFalseOrderByCreatedAtDesc(String accountId);

    Optional<Plan> findByIdAndIsDeletedFalse(String id);

    List<Plan> findAllByPlanStatusAndIsDeletedFalse(PlanStatus planStatus);

    Optional<Plan> findByAccountIdAndPlanStatusAndIsDeletedFalse(String accountId, PlanStatus planStatus);

    Optional<Plan> findFirstByAccountIdAndPlanStatusInAndIsDeletedFalse(
            String accountId,
            Collection<PlanStatus> statuses
    );

    List<Plan> findAllByAccountIdAndPlanStatusInAndIsDeletedFalse(
            String accountId,
            Collection<PlanStatus> statuses
    );
    Page<Plan> findAllByIsDeletedFalse(Pageable pageable);

    Optional<Plan> findByIdAndAccountIdAndIsDeletedFalse(String planId, String accountId);
}
