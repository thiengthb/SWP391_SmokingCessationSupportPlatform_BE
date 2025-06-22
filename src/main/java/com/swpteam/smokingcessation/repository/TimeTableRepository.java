package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.TimeTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, String> {

    Page<TimeTable> findAllByIsDeletedFalse(Pageable pageable);

    Page<TimeTable> findAllByCoach_IdAndIsDeletedFalse(String coachId, Pageable pageable);

    Page<TimeTable> findByCoach_IdAndIsDeletedFalse(String coachId, Pageable pageable);

    Optional<TimeTable> findByIdAndIsDeletedFalse(String id);

    Optional<TimeTable> findByCoach_IdAndStartedAtLessThanEqualAndEndedAtGreaterThanEqual(
            String coachId, LocalDateTime bookingStart, LocalDateTime bookingEnd
    );

}
