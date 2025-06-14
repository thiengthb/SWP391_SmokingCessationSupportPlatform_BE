package com.swpteam.smokingcessation.service.interfaces.notification;

import com.swpteam.smokingcessation.domain.dto.notification.NotificationRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationResponse;

public interface INotificationService {
    NotificationResponse sendNotification(NotificationRequest request);
}
