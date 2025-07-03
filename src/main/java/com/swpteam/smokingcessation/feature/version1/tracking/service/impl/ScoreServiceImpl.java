package com.swpteam.smokingcessation.feature.version1.tracking.service.impl;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.score.ScoreResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Score;
import com.swpteam.smokingcessation.domain.enums.ScoreRule;
import com.swpteam.smokingcessation.domain.mapper.ScoreMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.profile.service.IScoreService;
import com.swpteam.smokingcessation.repository.jpa.ScoreRepository;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Score updateScore(String accountId, ScoreRule point) {
        Score score = scoreRepository.findByAccountIdAndIsDeletedFalse(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.SCORE_NOT_FOUND));

        int current = score.getScore();
        int updated = Math.max(0, current + point.getPoint());

        score.setScore(updated);
        log.info("Updated score: [{}]", updated);
        Score saved = scoreRepository.save(score);
        updateRanking();
        updateLeaderboard();
        return saved;
    }

    @Override
    public List<ScoreResponse> getScoreList() {
        List<Score> scores = scoreRepository.findTop10ByAccountIsDeletedFalseOrderByScoreDesc();

        Account account = authUtilService.getCurrentAccountOrThrowError();
        boolean isInTop10 = scores.stream()
                .anyMatch(score -> score.getAccount().getId().equals(account.getId()));

        if (!isInTop10) {
            scores.add(getScoreByAccount(account));
        }

        return scores.stream()
                .map(scoreMapper::toResponse)
                .toList();
    }

    public void updateLeaderboard(){
        List<Score> scores = scoreRepository.findTop10ByAccountIsDeletedFalseOrderByScoreDesc();

        Account account = authUtilService.getCurrentAccountOrThrowError();
        boolean isInTop10 = scores.stream()
                .anyMatch(score -> score.getAccount().getId().equals(account.getId()));

        if (!isInTop10) {
            scores.add(getScoreByAccount(account));
        }

        List<ScoreResponse> leaderboardDto = scores.stream()
                .map(scoreMapper::toResponse)
                .toList();
        messagingTemplate.convertAndSend("/topic/leaderboard", leaderboardDto);
    }


    @Override
    public Score getScoreByAccount(Account account) {
        return scoreRepository.findByAccountIdAndIsDeletedFalse(account.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SCORE_NOT_FOUND));
    }

    @Transactional
    private void updateRanking(){
        List<Score> scores = scoreRepository.findAllByAccountIsDeletedFalseOrderByScoreDesc();

        int rank = 1;
        for(Score score : scores){
            score.setRank(rank);
            rank++;
            scoreRepository.save(score);
        }
    }
}
