package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
