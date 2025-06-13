package com.swpteam.smokingcessation.feature.service.impl.profile;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackCreateRequest;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackRespone;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthCreateRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.domain.dto.health.HealthUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Feedback;
import com.swpteam.smokingcessation.domain.entity.Health;
import com.swpteam.smokingcessation.domain.mapper.FeedbackMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.repository.AccountRepository;
import com.swpteam.smokingcessation.feature.repository.FeedbackRepository;
import com.swpteam.smokingcessation.feature.service.interfaces.profile.FeedbackService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackServiceImpl implements FeedbackService {
    FeedbackRepository feedbackRepository;
    AccountRepository accountRepository;
    FeedbackMapper feedbackMapper;

    @Override
    public Page<FeedbackRespone> getFeedbackPage(PageableRequest request){
        if(!ValidationUtil.isFieldExist(Feedback.class, request.getSortBy())) {
            throw  new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Feedback> feedbacks = feedbackRepository.findAllByIsDeletedFalse(pageable);

        return feedbacks.map(feedbackMapper::toRespone);
    }
    @Override
    public FeedbackRespone getFeedbackById(String id) {
        return feedbackMapper.toRespone(feedbackRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND)));
    }
    @Override
    public Page<FeedbackRespone> getFeedbackPageByAccountId(String accountId, PageableRequest request){
        if (!ValidationUtil.isFieldExist(Feedback.class, request.getSortBy())) {
            throw  new AppException(ErrorCode.INVALID_SORT_FIELD);
        }
        accountRepository.findById(accountId).
                orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
        Pageable pageable = PageableRequest.getPageable(request);
        Page<Feedback> feedbacks = feedbackRepository.findByAccountIdAndIsDeletedFalse(accountId, pageable);

        return feedbacks.map(feedbackMapper::toRespone);
    }
    @Override
    @Transactional
    public FeedbackRespone createFeedback(FeedbackCreateRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Feedback feedback = feedbackMapper.toEntity(request);

        feedback.setAccount(account);

        return feedbackMapper.toRespone(feedbackRepository.save(feedback));
    }
    @Override
    @Transactional
    public FeedbackRespone updateFeedback(String id, FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        feedbackMapper.updateFeedback(feedback, request);;

        return feedbackMapper.toRespone(feedbackRepository.save(feedback));
    }
    @Override
    @Transactional
    public void softDeleteFeedbackById(String id) {
        Feedback feedback = feedbackRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.HEALTH_RECORD_NOT_FOUND));

        feedback.setDeleted(true);

        feedbackRepository.save(feedback);
    }
}
