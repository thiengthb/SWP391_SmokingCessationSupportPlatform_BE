package com.swpteam.smokingcessation.feature.version1.notification.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationResponse;
import com.swpteam.smokingcessation.domain.entity.Notification;

import java.time.LocalDateTime;

public interface INotificationService {

    void sendNotification(NotificationRequest request);

    void sendApprovedNotification(String memberId,String coachUserName);

    void sendBookingNotification(String username, String coachId);

    void sendBookingRejectNotification(String content, String memberId);

    void markAsRead(String id);

    PageResponse<NotificationResponse> getMyNotificationsPage(PageableRequest request);

    NotificationResponse getNotificationsById(String id);

    void deleteNotification(String id);

    void deleteAllMyNotification();

    void sendPlanDoneNotification(String planName, String accountId);

    void sendPhaseDoneNotification(int phase, String accountId);

    Notification findNotificationByIdOrThrowError(String id);

   void  sendUpcomingBookingNotification(String memberId, LocalDateTime startBookingTime);


}
