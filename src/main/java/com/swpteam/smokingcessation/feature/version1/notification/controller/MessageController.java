package com.swpteam.smokingcessation.feature.version1.notification.controller;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.message.MessageRequest;
import com.swpteam.smokingcessation.domain.dto.message.MessageResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.feature.version1.notification.service.IMessageService;
import com.swpteam.smokingcessation.utils.ResponseUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Message", description = "Manage message-related operations")
public class MessageController {

    IMessageService messageService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getMessagePage(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MESSAGE_PAGE_FETCHED,
                messageService.getMessagePage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<MessageResponse>> getMessageById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MESSAGE_FETCHED_BY_ID,
                messageService.getById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<MessageResponse>> createMessage(
            @Valid @RequestBody MessageRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MESSAGE_CREATED,
                messageService.createMessage(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable String id,
            @Valid @RequestBody MessageRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MESSAGE_UPDATED,
                messageService.updateMessage(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable String id
    ) {
        messageService.softDeleteMessageById(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.MESSAGE_DELETED
        );
    }
    
}
