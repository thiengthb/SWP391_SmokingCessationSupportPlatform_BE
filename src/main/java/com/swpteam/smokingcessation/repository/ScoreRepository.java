package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, String> {
}
