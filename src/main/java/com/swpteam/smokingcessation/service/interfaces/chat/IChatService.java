package com.swpteam.smokingcessation.service.interfaces.chat;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.chat.ChatRequest;
import com.swpteam.smokingcessation.domain.dto.chat.ChatResponse;
import com.swpteam.smokingcessation.domain.dto.chat.ChatRestResponse;
import org.springframework.data.domain.Page;

public interface IChatService {

    PageResponse<ChatRestResponse> getChats(PageableRequest request);

    PageResponse<ChatRestResponse> getChatsById(String id, PageableRequest request);

    ChatResponse sendChatMessage(ChatRequest request);

    void softDeleteChat(String id);
}
