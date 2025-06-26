package com.swpteam.smokingcessation.repository;


import com.swpteam.smokingcessation.domain.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, String> {
    Optional<Score> findByAccountIdAndIsDeletedFalse(String accountId);

}
