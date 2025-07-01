package com.swpteam.smokingcessation.controller.v1.chat;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.chat.ChatRestResponse;
import com.swpteam.smokingcessation.feature.version1.chat.service.IChatService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
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
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<ChatResponse>>> getChats(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.CHAT_PAGE_FETCHED,
                chatService.getChats(request)
        );
    }

    @GetMapping("/{accountId}")
    ResponseEntity<ApiResponse<PageResponse<ChatResponse>>> getChatsById(
            @PathVariable String accountId,
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.CHAT_FETCHED_BY_ID,
                chatService.getChatsById(accountId, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteChat(
            @PathVariable String id
    ) {
        chatService.softDeleteChat(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.CHAT_DELETED
        );
    }
}
