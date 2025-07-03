package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Score;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;

public interface IScoreService {
    Score updateScore(String accountId, ScoreRule scoreRule);

    Score getScoreByAccount(Account account);
}
