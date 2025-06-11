package com.swpteam.smokingcessation.apis.record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID> {

    Optional<Record> findByDateAndAccount_Id(LocalDate date, String accountId);

    Page<Record> findByAccount_Email(String email, Pageable pageable);

    boolean existsByIdAndAccount_Email(UUID recordId, String email);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.id = :accountId AND a.email = :email AND a.isDeleted = false")
    boolean existsAccountByIdAndEmail(@Param("accountId") String accountId, @Param("email") String email);

    Optional<Record> findByDateAndAccount_Email(LocalDate date, String email);

    @Query("SELECT r FROM Record r WHERE r.account.email = :email AND r.date BETWEEN :startDate AND :endDate ORDER BY r.date DESC")
    Page<Record> findByAccount_EmailAndDateBetween(@Param("email") String email,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   Pageable pageable);
}