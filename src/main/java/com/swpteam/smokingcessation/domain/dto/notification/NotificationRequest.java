package com.swpteam.smokingcessation.domain.dto.notification;

import com.swpteam.smokingcessation.domain.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;

public record NotificationRequest (
        
    @NotBlank(message = "ACCOUNT_ID_REQUIRED")
    String accountId,

    @NotBlank(message = "NOTIFICATION_CONTENT_REQUIRED")
    String content,

    NotificationType notificationType
) {}
