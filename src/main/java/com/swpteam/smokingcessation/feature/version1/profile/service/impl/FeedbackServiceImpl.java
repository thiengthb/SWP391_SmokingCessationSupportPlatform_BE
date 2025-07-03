package com.swpteam.smokingcessation.feature.version1.profile.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackRequest;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Feedback;
import com.swpteam.smokingcessation.domain.mapper.FeedbackMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.FeedbackRepository;
import com.swpteam.smokingcessation.feature.version1.profile.service.IFeedbackService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackServiceImpl implements IFeedbackService {

    FeedbackRepository feedbackRepository;
    FeedbackMapper feedbackMapper;
    AuthUtilService authUtilService;

    @Override
    @Cacheable(value = "FEEDBACK_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction")
    public PageResponse<FeedbackResponse> getFeedbackPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Feedback.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Feedback> feedbacks = feedbackRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(feedbacks.map(feedbackMapper::toResponse));
    }

    @Override
    @Cacheable(value = "FEEDBACK_CACHE", key = "#id")
    public FeedbackResponse getFeedbackById(String id) {
        return feedbackMapper.toResponse(findFeedbackById(id));
    }

    @Override
    @Cacheable(value = "FEEDBACK_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction + '-' + #accountId")
    public PageResponse<FeedbackResponse> getFeedbackPageByAccountId(String accountId, PageableRequest request) {
        ValidationUtil.checkFieldExist(Feedback.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Feedback> feedbacks = feedbackRepository.findByAccountIdAndIsDeletedFalse(accountId, pageable);

        return new PageResponse<>(feedbacks.map(feedbackMapper::toResponse));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MEMBER', 'COACH')")
    @CachePut(value = "FEEDBACK_CACHE", key = "#result.getId()")
    @CacheEvict(value = "FEEDBACK_PAGE_CACHE", allEntries = true)
    public FeedbackResponse createFeedback(FeedbackRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Feedback feedback = feedbackMapper.toEntity(request);
        feedback.setAccount(currentAccount);

        return feedbackMapper.toResponse(feedbackRepository.save(feedback));
    }


    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MEMBER', 'COACH')")
    @CachePut(value = "FEEDBACK_CACHE", key = "#result.getId()")
    @CacheEvict(value = "FEEDBACK_PAGE_CACHE", allEntries = true)
    public FeedbackResponse updateFeedback(String id, FeedbackRequest request) {
        Feedback feedback = findFeedbackById(id);

        boolean haveAccess = authUtilService.isOwner(feedback.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        feedbackMapper.update(feedback, request);
        return feedbackMapper.toResponse(feedbackRepository.save(feedback));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"FEEDBACK_CACHE", "FEEDBACK_PAGE_CACHE"}, key = "#id", allEntries = true)
    public void softDeleteFeedbackById(String id) {
        Feedback feedback = findFeedbackById(id);

        boolean haveAccess = authUtilService.isAdminOrOwner(feedback.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        feedback.setDeleted(true);

        feedbackRepository.save(feedback);
    }

    @Cacheable(value = "FEEDBACK_CACHE", key = "#id")
    public Feedback findFeedbackById(String id) {
        Feedback feedback = feedbackRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        if (feedback.getAccount().isDeleted()) {
            feedback.setDeleted(true);
            feedbackRepository.save(feedback);
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return feedback;
    }

}
