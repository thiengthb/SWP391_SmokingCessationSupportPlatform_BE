package com.swpteam.smokingcessation.service.interfaces.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.review.ReviewCreateRequest;
import com.swpteam.smokingcessation.domain.dto.review.ReviewResponse;
import com.swpteam.smokingcessation.domain.dto.review.ReviewUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Review;
import org.springframework.data.domain.Page;

public interface IReviewService {

    PageResponse<ReviewResponse> getMyReviewPageAsMember(PageableRequest request);

    PageResponse<ReviewResponse> getMyReviewPageAsCoach(PageableRequest request);

    ReviewResponse getReviewById(String id);

    ReviewResponse createReview(ReviewCreateRequest request);

    ReviewResponse updateReview(String id, ReviewUpdateRequest request);

    void softDeleteReview(String id);

    Review findReviewById(String id);
}
