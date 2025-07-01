package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    Optional<Feedback> findByIdAndIsDeletedFalse(String id);

    Page<Feedback> findByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Page<Feedback> findAllByIsDeletedFalse(Pageable pageable);
}
