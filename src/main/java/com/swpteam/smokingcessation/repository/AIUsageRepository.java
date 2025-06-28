package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.AIUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AIUsageRepository extends JpaRepository<AIUsage, String> {

    Optional<AIUsage> findByAccountIdAndDate(String accountId, LocalDate date);
}
