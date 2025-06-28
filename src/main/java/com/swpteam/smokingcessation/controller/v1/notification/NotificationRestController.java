package com.swpteam.smokingcessation.controller.v1.notification;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationResponse;
import com.swpteam.smokingcessation.service.interfaces.notification.INotificationService;
import com.swpteam.smokingcessation.utils.ResponseUtil;
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
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Notification (Rest Controller)", description = "Manage notification-related(rest controller) operations")
public class NotificationRestController {
    INotificationService notificationService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getChats(
            @Valid PageableRequest request
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.NOTIFICATION_GET_ALL,
                notificationService.getMyNotificationsPage(request)
        );

    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<NotificationResponse>> getChatsById(
            @PathVariable String id
    ) {
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.NOTIFICATION_GET_BY_ID,
                notificationService.getNotificationsById(id)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteChat(
            @PathVariable String id
    ) {
        notificationService.deleteNotification(id);
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.NOTIFICATION_DELETED
        );
    }

    @DeleteMapping("/all/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteAllNotifications() {
        notificationService.deleteAllMyNotification();
        return ResponseUtil.buildSuccessResponse(
                SuccessCode.ALL_NOTIFICATION_DELETED
        );
    }

}
