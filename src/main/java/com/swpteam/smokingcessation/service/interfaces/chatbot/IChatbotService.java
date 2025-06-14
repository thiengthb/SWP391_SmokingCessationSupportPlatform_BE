package com.swpteam.smokingcessation.service.interfaces.chatbot;

import com.swpteam.smokingcessation.domain.dto.chatbot.ChatbotRequest;
import com.swpteam.smokingcessation.domain.dto.chatbot.ChatbotResponse;
import com.swpteam.smokingcessation.domain.entity.Account;

public interface IChatbotService {

    public ChatbotResponse chat(ChatbotRequest request);

    public int getAccountTokenLimit(Account account);

    public int estimateAITokenUsage(String content);
}
