package com.swpteam.smokingcessation.feature.version1.chat.service;

import com.swpteam.smokingcessation.domain.dto.chatbot.ChatbotRequest;
import com.swpteam.smokingcessation.domain.dto.chatbot.ChatbotResponse;
import com.swpteam.smokingcessation.domain.entity.Account;

public interface IChatbotService {

    ChatbotResponse chat(ChatbotRequest request);

    int getAccountTokenLimit(Account account);

    int estimateAITokenUsage(String content);
}
