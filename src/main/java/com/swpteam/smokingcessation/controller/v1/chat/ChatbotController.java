package com.swpteam.smokingcessation.controller.v1.chat;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.chatbot.ChatbotRequest;
import com.swpteam.smokingcessation.domain.dto.chatbot.ChatbotResponse;
import com.swpteam.smokingcessation.service.interfaces.chat.IChatbotService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbot")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Chatbot", description = "Manage chatbot operations")
public class ChatbotController {

    IChatbotService chatbotService;

    @PostMapping
    ResponseEntity<ApiResponse<ChatbotResponse>> createAccount(
            @RequestBody @Valid ChatbotRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.RETURN_MESSAGE,
                chatbotService.chat(request)
        );
    }
}
