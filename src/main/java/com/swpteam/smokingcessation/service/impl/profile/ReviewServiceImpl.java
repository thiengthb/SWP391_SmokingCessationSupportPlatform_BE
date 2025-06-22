package com.swpteam.smokingcessation.service.impl.profile;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.review.ReviewCreateRequest;
import com.swpteam.smokingcessation.domain.dto.review.ReviewResponse;
import com.swpteam.smokingcessation.domain.dto.review.ReviewUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Review;
import com.swpteam.smokingcessation.domain.mapper.ReviewMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.ReviewRepository;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.service.interfaces.profile.IReviewService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements IReviewService {

    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;
    AuthUtilService authUtilService;
    IAccountService accountService;

    @Override
    @Cacheable(value = "REVIEW_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction '-' + #memberId")
    public Page<ReviewResponse> getMyReviewPageAsMember(String memberId, PageableRequest request) {
        ValidationUtil.checkFieldExist(Review.class, request.sortBy());

        accountService.findAccountByIdOrThrowError(memberId);

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Review> reviews = reviewRepository.findByMemberIdAndIsDeletedFalse(memberId, pageable);

        return reviews.map(reviewMapper::toResponse);
    }

    @Override
    @Cacheable(value = "REVIEW_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction '-' + #coachId")
    public Page<ReviewResponse> getMyReviewPageAsCoach(String coachId, PageableRequest request) {
        ValidationUtil.checkFieldExist(Review.class, request.sortBy());

        accountService.findAccountByIdOrThrowError(coachId);

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Review> reviews = reviewRepository.findByCoachIdAndIsDeletedFalse(coachId, pageable);

        return reviews.map(reviewMapper::toResponse);
    }

    @Override
    @Cacheable(value = "REVIEW_CACHE", key = "#id")
    public ReviewResponse getReviewById(String id) {
        return reviewMapper.toResponse(findReviewById(id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    @CachePut(value = "REVIEW_CACHE", key = "#result.getId()")
    @CacheEvict(value = "REVIEW_PAGE_CACHE", allEntries = true)
    public ReviewResponse createReview(ReviewCreateRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Account coach = accountService.findAccountByIdOrThrowError(request.coachId());

        Review review = reviewMapper.toEntity(request);
        review.setMember(currentAccount);
        review.setCoach(coach);

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MEMBER')")
    @CachePut(value = "REVIEW_CACHE", key = "#result.getId()")
    @CacheEvict(value = "REVIEW_PAGE_CACHE", allEntries = true)
    public ReviewResponse updateReview(String id, ReviewUpdateRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Review review = findReviewById(id);
        if (!review.getMember().getId().equals(currentAccount.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        reviewMapper.update(review, request);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    @CacheEvict(value = {"REVIEW_CACHE", "REVIEW_PAGE_CACHE"}, key = "#id", allEntries = true)
    public void softDeleteReview(String id) {
        Review review = findReviewById(id);
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        if (authUtilService.isAdminOrOwner(currentAccount.getId()) || currentAccount == review.getCoach()) {
            review.setDeleted(true);
            reviewRepository.save(review);

        } else {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }

    @Override
    public Review findReviewById(String id) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (review.getMember().isDeleted() || review.getCoach().isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_DELETED);
        }

        return review;
    }

}
