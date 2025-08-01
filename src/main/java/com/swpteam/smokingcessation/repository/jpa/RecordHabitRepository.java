package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.RecordHabit;
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
public interface RecordHabitRepository extends JpaRepository<RecordHabit, String> {

    Optional<RecordHabit> findByIdAndIsDeletedFalse(String id);

    Optional<RecordHabit> findByAccountIdAndDateAndIsDeletedFalse(String accountId, LocalDate date);

    Page<RecordHabit> findByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Page<RecordHabit> findAllByIsDeletedFalse(Pageable pageable);

    boolean existsByAccountIdAndDateAndIsDeletedFalse(String id, LocalDate time);

    Optional<List<RecordHabit>> findAllByAccountIdAndDateBetweenAndIsDeletedFalse(String accountId, LocalDate start, LocalDate end);

    Optional<RecordHabit> findByAccountIdAndDate(String accountId, LocalDate date);

    Optional<RecordHabit> findTopByAccountIdAndDateLessThanOrderByDateDesc(String accountId, LocalDate date);

    @Query("SELECT r FROM RecordHabit r WHERE r.account.id = :accountId AND r.cigarettesSmoked = 0 AND r.isDeleted = false")
    List<RecordHabit> findAllByAccountIdWithNoCigarettesSmoked(@Param("accountId") String accountId);

}