package com.swpteam.smokingcessation.feature.version1.notification.controller;

import com.swpteam.smokingcessation.common.ApiResponse;
import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.SuccessCode;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationResponse;
import com.swpteam.smokingcessation.feature.version1.notification.service.INotificationService;
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
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Notification (Rest Controller)", description = "Manage notification-related(rest controller) operations")
public class NotificationRestController {

    INotificationService notificationService;
    ResponseUtilService responseUtilService;

    @GetMapping
    ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getNotifications(
            @Valid PageableRequest request
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.NOTIFICATION_PAGE_FETCHED,
                notificationService.getMyNotificationsPage(request)
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<NotificationResponse>> getNotificationsById(
            @PathVariable String id
    ) {
        return responseUtilService.buildSuccessResponse(
                SuccessCode.NOTIFICATION_FETCHED_BY_ID,
                notificationService.getNotificationsById(id)
        );
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable String id
    ) {
        notificationService.deleteNotification(id);
        return responseUtilService.buildSuccessResponse(
                SuccessCode.NOTIFICATION_DELETED
        );
    }

    @DeleteMapping("/all/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteAllNotifications() {
        notificationService.deleteAllMyNotification();
        return responseUtilService.buildSuccessResponse(
                SuccessCode.NOTIFICATION_ALL_DELETED
        );
    }

}
