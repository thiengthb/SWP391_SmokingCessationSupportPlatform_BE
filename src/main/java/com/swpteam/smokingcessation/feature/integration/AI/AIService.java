package com.swpteam.smokingcessation.feature.integration.AI;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AIService {

    ChatClient chatClient;

    public AIService(ChatClient.Builder builder) {
        chatClient = builder.build();
    }

    public String chat(String prompt) {
        return chatClient.prompt(prompt).call().content();
    }
}
