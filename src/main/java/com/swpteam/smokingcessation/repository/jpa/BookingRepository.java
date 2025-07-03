package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.Booking;
import com.swpteam.smokingcessation.domain.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    Optional<Booking> findByIdAndIsDeletedFalse(String id);

    Page<Booking> findAllByIsDeletedFalse(Pageable pageable);

    Page<Booking> findAllByMemberIdAndIsDeletedFalse(String memberId, Pageable pageable);

    Page<Booking> findAllByCoachIdAndIsDeletedFalse(String coachId, Pageable pageable);

    List<Booking> findAllByCoachIdAndStatusAndIsDeletedFalse(String coachId, BookingStatus status);

    List<Booking> findAllByMemberIdAndCoachIdAndIsDeletedFalse(String memberId,String coachId);

    boolean existsByCoachIdAndIsDeletedFalseAndStartedAtLessThanAndEndedAtGreaterThan(
            String coachId,
            LocalDateTime endedAt,
            LocalDateTime startedAt
    );
}