package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackRequest;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackResponse;

public interface IFeedbackService {

    PageResponse<FeedbackResponse> getFeedbackPage(PageableRequest request);

    PageResponse<FeedbackResponse> getFeedbackPageByAccountId(String accountId, PageableRequest request);

    FeedbackResponse getFeedbackById(String id);

    FeedbackResponse createFeedback(FeedbackRequest request);

    FeedbackResponse updateFeedback(String id, FeedbackRequest request);

    void softDeleteFeedbackById(String id);

}
