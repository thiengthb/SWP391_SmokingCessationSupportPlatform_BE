package com.swpteam.smokingcessation.service.impl.notification;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.notification.MarkAsReadRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Notification;
import com.swpteam.smokingcessation.domain.mapper.NotificationMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.NotificationRepository;
import com.swpteam.smokingcessation.service.interfaces.notification.INotificationService;
import com.swpteam.smokingcessation.utils.AuthorizationUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements INotificationService {

    SimpMessagingTemplate simpMessagingTemplate;
    NotificationMapper notificationMapper;
    NotificationRepository notificationRepository;
    AccountRepository accountRepository;
    AuthorizationUtilService authorizationUtilService;

    @Override
    public void sendNotification(NotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        Account account = null;
        if (request.getAccountId() != null) {
            account = accountRepository.findByIdAndIsDeletedFalse(request.getAccountId()).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        }
        notification.setAccount(account);
        notification.setSentAt(LocalDateTime.now());
        notification.setRead(false);

        notificationRepository.save(notification);

        NotificationResponse response = notificationMapper.toResponse(notification);

        if (request.getAccountId() == null) {
            simpMessagingTemplate.convertAndSend("/topic/notifications/", response);
        } else {
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + request.getAccountId(), response);
        }
    }

    @Override
    public void markAsRead(MarkAsReadRequest request) {
        Notification notification = notificationRepository.findByIdAndIsDeletedFalse(request.getNotificationId())
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<NotificationResponse> getNotifications(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Account.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }
        Pageable pageable = PageableRequest.getPageable(request);
        Page<Notification> notifications = notificationRepository.findAllByIsDeletedFalse(pageable);

        return notifications.map(notificationMapper::toResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<NotificationResponse> getNotificationsById(String id, PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Account.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }
        Pageable pageable = PageableRequest.getPageable(request);
        Page<Notification> notifications = notificationRepository.findByAccountIdAndIsDeletedFalse(id, pageable);

        return notifications.map(notificationMapper::toResponse);
    }

    @Override
    public void deleteNotification(String id) {
        Notification notification = notificationRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        boolean haveAccess = authorizationUtilService.checkAdminOrOwner(notification.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.OTHERS_NOTIFICATION_CANT_DELETE);
        }

        notification.setDeleted(true);
        notificationRepository.save(notification);
    }

    public void deleteAllNotification(String id) {
        List<Notification> notifications = notificationRepository.findAllByAccountIdAndIsDeletedFalse(id);

        if (notifications.isEmpty()) {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        boolean haveAccess = authorizationUtilService.checkAdminOrOwner(notifications.getFirst().getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.OTHERS_NOTIFICATION_CANT_DELETE);
        }

        notifications.forEach(notification -> notification.setDeleted(true));
        notificationRepository.saveAll(notifications);
    }
}
