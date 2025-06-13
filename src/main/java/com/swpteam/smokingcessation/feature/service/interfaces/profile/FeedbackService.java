package com.swpteam.smokingcessation.feature.service.interfaces.profile;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackCreateRequest;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackRespone;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackUpdateRequest;
import org.springframework.data.domain.Page;

public interface FeedbackService {
    public Page<FeedbackRespone> getFeedbackPage(PageableRequest request);

    public FeedbackRespone getFeedbackById(String id);

    public Page<FeedbackRespone> getFeedbackPageByAccountId(String accountId, PageableRequest request);

    public FeedbackRespone createFeedback(FeedbackCreateRequest request);

    public FeedbackRespone updateFeedback(String id, FeedbackUpdateRequest request);

    public void softDeleteFeedbackById(String id);

}
