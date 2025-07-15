package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.Health;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthRepository extends JpaRepository<Health, String> {

    Optional<Health> findByIdAndIsDeletedFalse(String id);

    Optional<Health> findFirstByAccountIdAndIsDeletedFalseOrderByCreatedAtDesc(String accountId);

    Page<Health> findByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Page<Health> findAllByIsDeletedFalse(Pageable pageable);

    Page<Health> findAllByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    boolean existsByAccountId(String accountId);

    List<Health> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Optional<Health> findFirstByAccountIdAndIsDeletedFalseOrderByCreatedAtAsc(String accountId);


}
