package com.swpteam.smokingcessation.controller.v1.profile;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.review.ReviewCreateRequest;
import com.swpteam.smokingcessation.domain.dto.review.ReviewResponse;
import com.swpteam.smokingcessation.domain.dto.review.ReviewUpdateRequest;
import com.swpteam.smokingcessation.service.interfaces.profile.IReviewService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    @GetMapping("/coach/my-review")
    ResponseEntity<ApiResponse<Page<ReviewResponse>>> getMyReviewPageAsCoach(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.REVIEW_GET_ALL,
                reviewService.getMyReviewPageAsCoach(request)
        );
    }

    @GetMapping("/member/my-review")
    ResponseEntity<ApiResponse<Page<ReviewResponse>>> getMyReviewPageAsMember(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.REVIEW_GET_ALL,
                reviewService.getMyReviewPageAsMember(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.REVIEW_GET_BY_ID,
                reviewService.getReviewById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestBody @Valid ReviewCreateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.REVIEW_CREATED,
                reviewService.createReview(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable String id,
            @RequestBody @Valid ReviewUpdateRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.REVIEW_UPDATED,
                reviewService.updateReview(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> softDeleteReview(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.REVIEW_DELETED,
                null
        );
    }
    
}
