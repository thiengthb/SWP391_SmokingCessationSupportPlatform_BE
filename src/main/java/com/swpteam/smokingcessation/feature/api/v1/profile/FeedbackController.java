package com.swpteam.smokingcessation.feature.api.v1.profile;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackCreateRequest;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackRespone;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackUpdateRequest;
import com.swpteam.smokingcessation.feature.service.interfaces.profile.FeedbackService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FeedbackController {
    FeedbackService feedbackService;

    @GetMapping
    ResponseEntity<ApiResponse<Page<FeedbackRespone>>> getFeedbackPage(@Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<FeedbackRespone>>builder()
                        .code(SuccessCode.FEEDBACK_GET_ALL.getCode())
                        .message(SuccessCode.FEEDBACK_GET_ALL.getMessage())
                        .result(feedbackService.getFeedbackPage(request))
                        .build()
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<FeedbackRespone>> getFeedbackById(@PathVariable String id) {
        return ResponseEntity.ok(
                ApiResponse.<FeedbackRespone>builder()
                        .code(SuccessCode.FEEDBACK_GET_BY_ID.getCode())
                        .message(SuccessCode.FEEDBACK_GET_BY_ID.getMessage())
                        .result(feedbackService.getFeedbackById(id))
                        .build()
        );
    }

    @GetMapping("/account/{id}")
    ResponseEntity<ApiResponse<Page<FeedbackRespone>>> getFeedbackPageByAccountId(@PathVariable String id, @jakarta.validation.Valid PageableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<Page<FeedbackRespone>>builder()
                        .code(SuccessCode.FEEDBACK_GET_BY_ACCOUNT.getCode())
                        .message(SuccessCode.FEEDBACK_GET_BY_ACCOUNT.getMessage())
                        .result(feedbackService.getFeedbackPageByAccountId(id, request))
                        .build()
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<FeedbackRespone>> createFeedback(@RequestBody @jakarta.validation.Valid FeedbackCreateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<FeedbackRespone>builder()
                        .code(SuccessCode.FEEDBACK_CREATED.getCode())
                        .message(SuccessCode.FEEDBACK_CREATED.getMessage())
                        .result(feedbackService.createFeedback(request))
                        .build()
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<FeedbackRespone>> updateFeedback(@PathVariable String id, @RequestBody @jakarta.validation.Valid FeedbackUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<FeedbackRespone>builder()
                        .code(SuccessCode.FEEDBACK_UPDATED.getCode())
                        .message(SuccessCode.FEEDBACK_UPDATED.getMessage())
                        .result(feedbackService.updateFeedback(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> deleteFeedbackById(@PathVariable String id) {
        feedbackService.softDeleteFeedbackById(id);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .code(SuccessCode.FEEDBACK_DELETED.getCode())
                        .message(SuccessCode.FEEDBACK_DELETED.getMessage())
                        .build()
        );
    }

}
