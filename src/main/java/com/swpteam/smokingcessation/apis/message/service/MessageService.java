package com.swpteam.smokingcessation.apis.message.service;

import com.swpteam.smokingcessation.apis.message.dto.request.MessageRequest;
import com.swpteam.smokingcessation.apis.message.dto.response.MessageResponse;

import java.util.List;

public interface MessageService {
    MessageResponse createMessage(MessageRequest request);
    MessageResponse updateMessage(String id, MessageRequest request);
    List<MessageResponse> getAllMessages();
    void deleteMessage(String id);
    MessageResponse searchById(String id); //

}

