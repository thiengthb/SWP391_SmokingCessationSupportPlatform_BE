package com.swpteam.smokingcessation.feature.version1.profile.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.review.ReviewCreateRequest;
import com.swpteam.smokingcessation.domain.dto.review.ReviewResponse;
import com.swpteam.smokingcessation.domain.dto.review.ReviewUpdateRequest;
import com.swpteam.smokingcessation.feature.version1.profile.service.IReviewService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Review", description = "Manage review-related operations")
public class ReviewController {

    IReviewService reviewService;
    ResponseUtilService  responseUtilService;

    @GetMapping("/coach/my-review")
    ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getMyReviewPageAsCoach(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.REVIEW_PAGE_FETCHED,
                reviewService.getMyReviewPageAsCoach(request)
        );
    }

    @GetMapping("/member/my-review")
    ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getMyReviewPageAsMember(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.REVIEW_PAGE_FETCHED,
                reviewService.getMyReviewPageAsMember(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.REVIEW_FETCHED_BY_ID,
                reviewService.getReviewById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestBody @Valid ReviewCreateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.REVIEW_CREATED,
                reviewService.createReview(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable String id,
            @RequestBody @Valid ReviewUpdateRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.REVIEW_UPDATED,
                reviewService.updateReview(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> softDeleteReview(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.REVIEW_DELETED
        );
    }
    
}
