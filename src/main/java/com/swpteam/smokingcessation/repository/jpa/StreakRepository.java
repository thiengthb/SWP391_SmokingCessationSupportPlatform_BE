package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.Streak;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreakRepository extends JpaRepository<Streak, String> {

    Page<Streak> findAllByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    Optional<Streak> findByAccountIdAndIsDeletedFalse(String accountId);

}
