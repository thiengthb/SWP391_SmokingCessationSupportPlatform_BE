package com.swpteam.smokingcessation.controller.v1.notification;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.message.MessageRequest;
import com.swpteam.smokingcessation.domain.dto.message.MessageResponse;
import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.service.interfaces.notification.IMessageService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getMessagePage(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.MEMBERSHIP_GET_ALL,
                messageService.getMessagePage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<MessageResponse>> getMessageById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.MESSAGE_GET_BY_ID,
                messageService.getById(id)
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<MessageResponse>> createMessage(
            @Valid @RequestBody MessageRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.MESSAGE_CREATED,
                messageService.createMessage(request)
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<MessageResponse>> updateMessage(
            @PathVariable String id,
            @Valid @RequestBody MessageRequest request
    ) {
        return ResponseUtil.buildResponse(
                SuccessCode.MESSAGE_UPDATED,
                messageService.updateMessage(id, request)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable String id
    ) {
        messageService.softDeleteMessageById(id);
        return ResponseUtil.buildResponse(
                SuccessCode.MESSAGE_DELETED,
                null
        );
    }
    
}
