package com.swpteam.smokingcessation.feature.version1.tracking.service.impl;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.score.ScoreLeaderboardWrapper;
import com.swpteam.smokingcessation.domain.dto.score.ScoreResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Score;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.domain.mapper.ScoreMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.profile.service.IScoreService;
import com.swpteam.smokingcessation.feature.version1.tracking.service.ILeaderboardCacheService;
import com.swpteam.smokingcessation.repository.jpa.ScoreRepository;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreServiceImpl implements IScoreService {

    ScoreRepository scoreRepository;
    ScoreMapper scoreMapper;
    AuthUtilService authUtilService;
    SimpMessagingTemplate messagingTemplate;
    ILeaderboardCacheService leaderboardCacheService;


    @Override
    public Score updateScore(String accountId, ScoreRule point) {
        Score score = scoreRepository.findByAccountIdAndIsDeletedFalse(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.SCORE_NOT_FOUND));

        int current = score.getScore();
        int updated = Math.max(0, current + point.getPoint());

        if (updated > current) {
            score.setScore(updated);
            score.setScoreAchievedAt(LocalDateTime.now());
        }
        log.info("Updated score: [{}]", updated);
        Score saved = scoreRepository.save(score);
        updateRanking();
        updateLeaderboard();
        return saved;
    }

    @Override
    public List<ScoreResponse> getScoreList() {
        ScoreLeaderboardWrapper wrapper = leaderboardCacheService.getLeaderboardWrapper();
        List<ScoreResponse> cachedLeaderboard = new ArrayList<>(wrapper.getLeaderboard()); // ✅ Fix here

        Account account = authUtilService.getCurrentAccount().orElse(null);
        if (account == null) {
            return cachedLeaderboard;
        }

        boolean isInTop10 = cachedLeaderboard.stream()
                .filter(score -> score != null && score.getUsername() != null)
                .anyMatch(score -> score.getUsername().equals(account.getUsername()));

        if (!isInTop10) {
            Score myScore = getScoreByAccount(account);
            cachedLeaderboard.add(scoreMapper.toResponse(myScore));
        }

        return cachedLeaderboard;
    }

    public void updateLeaderboard() {
        ScoreLeaderboardWrapper wrapper = leaderboardCacheService.updateLeaderboardWrapper();
        List<ScoreResponse> cachedLeaderboard = wrapper.getLeaderboard();

        Account account = authUtilService.getCurrentAccountOrThrowError();

        boolean isInTop10 = cachedLeaderboard.stream()
                .filter(score -> score != null && score.getUsername() != null)
                .anyMatch(score -> score.getUsername().equals(account.getUsername()));

        if (!isInTop10) {
            cachedLeaderboard.add(scoreMapper.toResponse(getScoreByAccount(account)));
        }

        messagingTemplate.convertAndSend("/topic/leaderboard", cachedLeaderboard);
    }


    @Override
    public Score getScoreByAccount(Account account) {
        return scoreRepository.findByAccountIdAndIsDeletedFalse(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SCORE_NOT_FOUND));
    }

    @Transactional
    private void updateRanking() {
        List<Score> scores = scoreRepository.findAllByAccountIsDeletedFalseOrderByScoreDescScoreAchievedAtAsc();

        int rank = 1;
        for (Score score : scores) {
            score.setRank(rank++);
        }
        scoreRepository.saveAll(scores);
    }

}
