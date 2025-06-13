package com.swpteam.smokingcessation.domain.mapper;

import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackCreateRequest;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackRespone;
import com.swpteam.smokingcessation.domain.dto.feedback.FeedbackUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    Feedback toEntity(FeedbackCreateRequest request);

    @Mapping(source = "account.id", target = "accountId")
    FeedbackRespone toRespone(Feedback feedback);
    void updateFeedback(@MappingTarget Feedback feedback, FeedbackUpdateRequest request);
}
