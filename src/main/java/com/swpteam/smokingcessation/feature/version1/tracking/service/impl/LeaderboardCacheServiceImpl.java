package com.swpteam.smokingcessation.feature.version1.tracking.service.impl;

import com.swpteam.smokingcessation.domain.dto.score.ScoreLeaderboardWrapper;
import com.swpteam.smokingcessation.domain.dto.score.ScoreResponse;
import com.swpteam.smokingcessation.domain.entity.Score;
import com.swpteam.smokingcessation.domain.mapper.ScoreMapper;
import com.swpteam.smokingcessation.feature.version1.tracking.service.ILeaderboardCacheService;
import com.swpteam.smokingcessation.repository.jpa.ScoreRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LeaderboardCacheServiceImpl implements ILeaderboardCacheService {
    ScoreRepository scoreRepository;
    ScoreMapper scoreMapper;

    //Wrap the list into a simple wrapper => deserialize
    @Cacheable(value = "LEADERBOARD_CACHE", key = "'top10'")
    @Override
    public ScoreLeaderboardWrapper getLeaderboardWrapper() {
        List<Score> scores = scoreRepository.findTop10ByAccountIsDeletedFalseOrderByRankAsc();
        List<ScoreResponse> leaderboard = scores.stream()
                .map(scoreMapper::toResponse)
                .toList();

        log.info("No CACHE found, hitting the database for data");

        ScoreLeaderboardWrapper wrapper = new ScoreLeaderboardWrapper();
        wrapper.setLeaderboard(leaderboard);
        return wrapper;
    }

    @CachePut(value = "LEADERBOARD_CACHE", key = "'top10'")
    @Override
    public ScoreLeaderboardWrapper updateLeaderboardWrapper() {
        List<Score> scores = scoreRepository.findTop10ByAccountIsDeletedFalseOrderByRankAsc();
        List<ScoreResponse> leaderboard = scores.stream()
                .map(scoreMapper::toResponse)
                .toList();

        ScoreLeaderboardWrapper wrapper = new ScoreLeaderboardWrapper();
        wrapper.setLeaderboard(leaderboard);
        return wrapper;
    }
}
