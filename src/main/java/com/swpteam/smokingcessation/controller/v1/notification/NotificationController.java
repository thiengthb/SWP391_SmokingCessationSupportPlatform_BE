package com.swpteam.smokingcessation.controller.v1.notification;


import com.swpteam.smokingcessation.domain.dto.notification.NotificationRequest;
import com.swpteam.smokingcessation.service.interfaces.notification.INotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Notification", description = "Manage notification-related operations")
public class NotificationController {

    INotificationService notificationService;

    @MessageMapping("/notifications/send")
    public void sendNotification(
            @Valid @Payload NotificationRequest request
    ) {
        notificationService.sendNotification(request);
    }

    @MessageMapping("/notifications/mark-as-read/{id}")
    public void markAsRead(
            @PathVariable String id
    ) {
        notificationService.markAsRead(id);
    }

}
