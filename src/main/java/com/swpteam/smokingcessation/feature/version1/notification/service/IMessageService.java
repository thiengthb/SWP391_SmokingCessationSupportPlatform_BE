package com.swpteam.smokingcessation.feature.version1.notification.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.message.MessageRequest;
import com.swpteam.smokingcessation.domain.dto.message.MessageResponse;
import com.swpteam.smokingcessation.domain.entity.Message;

public interface IMessageService {

    PageResponse<MessageResponse> getMessagePage(PageableRequest request);

    MessageResponse getById(String id);

    MessageResponse createMessage(MessageRequest request);

    MessageResponse updateMessage(String id, MessageRequest request);

    void softDeleteMessageById(String id);

    Message findMessageByIdOrThrowError(String id);
}
