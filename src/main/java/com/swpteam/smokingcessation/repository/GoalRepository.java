package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, String> {

    Optional<Goal> findByIdAndIsDeletedFalse(String id);

    Optional<Goal> findByNameAndIsDeletedFalse(String name);

    Page<Goal> findAllByAccountIsNullAndIsDeletedFalse(Pageable pageable);

    Page<Goal> findAllByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    boolean existsByNameAndIsDeletedFalse(String name);
}

