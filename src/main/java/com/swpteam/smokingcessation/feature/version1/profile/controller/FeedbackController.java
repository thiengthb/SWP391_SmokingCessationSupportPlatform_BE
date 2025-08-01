package com.swpteam.smokingcessation.feature.version1.profile.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackRequest;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackResponse;
import com.swpteam.smokingcessation.feature.version1.profile.service.IFeedbackService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Feedback", description = "Manage feedback catalog")
public class FeedbackController {

    IFeedbackService feedbackService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<FeedbackResponse>>> getFeedbackPage(@Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.FEEDBACK_PAGE_FETCHED,
                feedbackService.getFeedbackPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<FeedbackResponse>> getFeedbackById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.FEEDBACK_FETCHED_BY_ID,
                feedbackService.getFeedbackById(id)
        );
    }

    @GetMapping("/account/{id}")
    ResponseEntity<ApiResponse<PageResponse<FeedbackResponse>>> getFeedbackPageByAccountId(
            @PathVariable String id,
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.FEEDBACK_FETCHED_BY_ACCOUNT,
                feedbackService.getFeedbackPageByAccountId(id, request)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<FeedbackResponse>> createFeedback(
            @RequestBody @Valid FeedbackRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.FEEDBACK_CREATED,
                feedbackService.createFeedback(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<FeedbackResponse>> updateFeedback(
            @PathVariable String id,
            @RequestBody @Valid FeedbackRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.FEEDBACK_UPDATED,
                feedbackService.updateFeedback(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteFeedbackById(@PathVariable String id) {
        feedbackService.softDeleteFeedbackById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.FEEDBACK_DELETED
        );
    }

}
