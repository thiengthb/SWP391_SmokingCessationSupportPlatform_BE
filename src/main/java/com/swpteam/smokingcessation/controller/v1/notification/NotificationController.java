package com.swpteam.smokingcessation.controller.v1.notification;


import com.swpteam.smokingcessation.domain.dto.notification.NotificationRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationResponse;
import com.swpteam.smokingcessation.service.impl.notification.NotificationServiceImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class NotificationController {
    NotificationServiceImpl notificationService;


    // /app/notify
    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public NotificationResponse sendNotification(@Valid @Payload NotificationRequest request) {
        System.out.println("WebSocket received: " + request);
        return notificationService.sendNotification(request);
    }
}
