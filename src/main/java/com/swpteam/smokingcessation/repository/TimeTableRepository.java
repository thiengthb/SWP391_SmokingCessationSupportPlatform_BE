package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.TimeTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable,String> {

    Page<TimeTable> findAllByIsDeletedFalse(Pageable pageable);

    Page<TimeTable> findAllByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Page<TimeTable> findByCoachIdAndIsDeletedFalse(String coachId, Pageable pageable);

    Optional<TimeTable> findByIdAndIsDeletedFalse(String id);

    Optional<TimeTable> findByCoachIdAndStartedAtLessThanEqualAndEndedAtGreaterThanEqual(
            String coachId, LocalDateTime bookingStart, LocalDateTime bookingEnd
    );

}
