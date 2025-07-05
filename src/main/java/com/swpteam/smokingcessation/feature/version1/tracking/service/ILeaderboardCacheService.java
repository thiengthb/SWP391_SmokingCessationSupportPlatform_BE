package com.swpteam.smokingcessation.feature.version1.tracking.service;

import com.swpteam.smokingcessation.domain.dto.score.ScoreLeaderboardWrapper;
import org.springframework.cache.annotation.CachePut;

public interface ILeaderboardCacheService {

    ScoreLeaderboardWrapper getLeaderboardWrapper();

    @CachePut(value = "LEADERBOARD_CACHE", key = "'top10'")
    ScoreLeaderboardWrapper updateLeaderboardWrapper();
}
