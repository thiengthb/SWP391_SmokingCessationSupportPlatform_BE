package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.domain.dto.score.ScoreResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Score;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;

import java.util.List;

public interface IScoreService {
    Score updateScore(String accountId, ScoreRule scoreRule);

    List<ScoreResponse> getScoreList();

    Score getScoreByAccount(Account account);
}
