package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByIdAndIsDeletedFalse(String id);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end")
    double sumAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
