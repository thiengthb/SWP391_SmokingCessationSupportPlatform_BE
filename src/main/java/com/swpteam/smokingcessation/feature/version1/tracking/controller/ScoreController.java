package com.swpteam.smokingcessation.feature.version1.tracking.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.score.ScoreResponse;
import com.swpteam.smokingcessation.feature.version1.profile.service.IScoreService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/scores")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Score", description = "Manage score-related operations")
public class ScoreController {

    IScoreService scoreService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<List<ScoreResponse>>> getScores() {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.ACCOUNT_CREATED,
                scoreService.getScoreList()
        );
    }
}
