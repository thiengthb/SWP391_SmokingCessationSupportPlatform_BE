package com.swpteam.smokingcessation.domain.dto.notification;

import jakarta.validation.constraints.NotBlank;

public record MarkAsReadRequest (

    @NotBlank(message = "NOTIFICATION_ID_REQUIRED")
    String notificationId
) {}
