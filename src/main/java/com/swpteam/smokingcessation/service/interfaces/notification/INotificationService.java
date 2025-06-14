package com.swpteam.smokingcessation.service.interfaces.notification;

import com.swpteam.smokingcessation.domain.dto.notification.MarkAsReadRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationRequest;

public interface INotificationService {
    void sendNotification(NotificationRequest request);

    void markAsRead(MarkAsReadRequest request);
}
