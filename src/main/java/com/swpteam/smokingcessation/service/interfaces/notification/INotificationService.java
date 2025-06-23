package com.swpteam.smokingcessation.service.interfaces.notification;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.notification.MarkAsReadRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationResponse;
import com.swpteam.smokingcessation.domain.entity.Notification;
import org.springframework.data.domain.Page;

public interface INotificationService {

    void sendNotification(NotificationRequest request);

    void markAsRead(String id);

    PageResponse<NotificationResponse> getMyNotificationsPage(PageableRequest request);

    NotificationResponse getNotificationsById(String id);

    void deleteNotification(String id);

    void deleteAllMyNotification();

    Notification findNotificationByIdOrThrowError(String id);
}
