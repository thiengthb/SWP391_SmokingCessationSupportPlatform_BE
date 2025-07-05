package com.swpteam.smokingcessation.feature.version1.tracking.service.impl;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Score;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.ScoreRepository;
import com.swpteam.smokingcessation.feature.version1.profile.service.IScoreService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreServiceImpl implements IScoreService {
    ScoreRepository scoreRepository;

    @Override
    public Score updateScore(String accountId, ScoreRule point) {
        Score score = scoreRepository.findByAccountIdAndIsDeletedFalse(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.SCORE_NOT_FOUND));

        int current = score.getScore();
        int updated = Math.max(0, current + point.getPoint());

        score.setScore(updated);
        log.info("Updated score: [{}]", updated);
        return scoreRepository.save(score);
    }

    @Override
    public Score getScoreByAccount(Account account) {
        return null;
    }
}
