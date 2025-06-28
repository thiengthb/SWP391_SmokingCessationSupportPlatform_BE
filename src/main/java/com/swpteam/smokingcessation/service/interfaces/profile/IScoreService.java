package com.swpteam.smokingcessation.service.interfaces.profile;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Score;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;

public interface IScoreService {
    Score updateScore(String accountId, ScoreRule scoreRule);

    Score getScoreByAccount(Account account);
}
