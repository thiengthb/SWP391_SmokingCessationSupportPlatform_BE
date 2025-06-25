package com.swpteam.smokingcessation.controller.v1.chat;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.chat.ChatRestResponse;
import com.swpteam.smokingcessation.service.interfaces.chat.IChatService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatRestController {
    
    IChatService chatService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<ChatRestResponse>>> getChats(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.CHAT_GET_ALL,
                chatService.getChats(request)
        );
    }

    @GetMapping("/{accountId}")
    ResponseEntity<ApiResponse<PageResponse<ChatRestResponse>>> getChatsById(
            @PathVariable String accountId,
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.CHAT_GET_BY_ID,
                chatService.getChatsById(accountId, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteChat(
            @PathVariable String id
    ) {
        chatService.softDeleteChat(id);
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.CHAT_DELETED,
                null
        );
    }
}
