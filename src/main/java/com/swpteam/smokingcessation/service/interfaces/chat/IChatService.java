package com.swpteam.smokingcessation.service.interfaces.chat;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.chat.ChatRequest;
import com.swpteam.smokingcessation.domain.dto.chat.ChatResponse;

public interface IChatService {

    PageResponse<ChatResponse> getChats(PageableRequest request);

    PageResponse<ChatResponse> getChatsById(String id, PageableRequest request);

    ChatResponse sendChatMessage(ChatRequest request);

    void softDeleteChat(String id);
}
