package com.swpteam.smokingcessation.apis.message.controller;


import com.swpteam.smokingcessation.apis.message.dto.request.MessageRequest;
import com.swpteam.smokingcessation.apis.message.dto.response.MessageResponse;
import com.swpteam.smokingcessation.apis.message.service.MessageService;
import com.swpteam.smokingcessation.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ApiResponse<MessageResponse> createMessage(@Valid @RequestBody MessageRequest request) {
        MessageResponse response = messageService.createMessage(request);
        return ApiResponse.<MessageResponse>builder()
                .code(201)
                .message("Message created successfully")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<MessageResponse> updateMessage(
            @PathVariable String id,
            @Valid @RequestBody MessageRequest request) {
        MessageResponse response = messageService.updateMessage(id, request);
        return ApiResponse.<MessageResponse>builder()
                .code(200)
                .message("Message updated successfully")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<MessageResponse>> getAllMessages() {
        List<MessageResponse> responses = messageService.getAllMessages();
        return ApiResponse.<List<MessageResponse>>builder()
                .code(200)
                .message("All messages retrieved")
                .result(responses)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<MessageResponse> getMessageById(@PathVariable String id) {
        MessageResponse response = messageService.searchById(id);
        return ApiResponse.<MessageResponse>builder()
                .code(200)
                .message("Message retrieved")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMessage(@PathVariable String id) {
        messageService.deleteMessage(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Message deleted successfully")
                .build();
    }
}
