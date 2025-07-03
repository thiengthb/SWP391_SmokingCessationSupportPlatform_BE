package com.swpteam.smokingcessation.feature.version1.notification.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Notification;
import com.swpteam.smokingcessation.domain.enums.NotificationType;
import com.swpteam.smokingcessation.domain.mapper.NotificationMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.NotificationRepository;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements INotificationService {

    SimpMessagingTemplate simpMessagingTemplate;
    NotificationMapper notificationMapper;
    NotificationRepository notificationRepository;

    IAccountService accountService;
    AuthUtilService authUtilService;

    @Override
    @Transactional
    @CachePut(value = "NOTIFICATION_CACHE", key = "#result.getId()")
    @CacheEvict(value = "NOTIFICATION_PAGE_CACHE", allEntries = true)
    public void sendNotification(NotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);

        Account account = request.accountId() != null ?
                accountService.findAccountByIdOrThrowError(request.accountId())
                :
                null;

        notification.setAccount(account);
        notification.setSentAt(LocalDateTime.now());
        notification.setRead(false);

        NotificationResponse response = notificationMapper.toResponse(notificationRepository.save(notification));

        if (request.accountId() == null) {
            simpMessagingTemplate.convertAndSend("/topic/notifications/", response);
        } else {
            log.info("Notification sent to topic: /topic/notifications/{}", request.accountId());
            simpMessagingTemplate.convertAndSend("/topic/notifications/" + request.accountId(), response);
        }
    }

    @Override
    public void sendApprovedNotification(String memberId,String coachUserName) {
        NotificationRequest request = new NotificationRequest(
                memberId,
                "Your booking with coach " + coachUserName +" have been approved",
                NotificationType.LIVE);
        sendNotification(request);
    }

    @Override
    public void sendBookingNotification(String username, String coachId) {
        NotificationRequest request = new NotificationRequest(
                coachId,
                "You have a new booking from " + username,
                NotificationType.LIVE);
        sendNotification(request);
    }

    @Override
    public void sendBookingRejectNotification(String content, String memberId) {
        NotificationRequest request = new NotificationRequest(
                memberId,
                content,
                NotificationType.LIVE
        );
        sendNotification(request);
    }

    @Override
    public void sendPlanDoneNotification(String planName, String accountId) {
        NotificationRequest request = new NotificationRequest(
                accountId,
                "Plan " + planName + " has completed",
                NotificationType.LIVE);
        sendNotification(request);
    }

    @Override
    public void sendPhaseDoneNotification(int phase, String accountId) {
        NotificationRequest request = new NotificationRequest(
                accountId,
                "phase " +  phase + "has completed",
                NotificationType.LIVE);
        sendNotification(request);
    }

    @Override
    @Transactional
    @CachePut(value = "NOTIFICATION_CACHE", key = "#result.getId()")
    @CacheEvict(value = "NOTIFICATION_PAGE_CACHE", allEntries = true)
    public void markAsRead(String id) {
        Notification notification = findNotificationByIdOrThrowError(id);

        boolean haveAccess = authUtilService.isOwner(notification.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        } else {
            throw new AppException(ErrorCode.NOTIFICATION_ALREADY_READ);
        }
    }

    @Override
    @Cacheable(value = "NOTIFICATION_PAGE_CACHE",
            key = "#request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.direction + '-' + @authUtilService.getCurrentAccountOrThrowError().id")
    public PageResponse<NotificationResponse> getMyNotificationsPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Notification.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Notification> notifications = notificationRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(notifications.map(notificationMapper::toResponse));
    }

    @Override
    @Cacheable(value = "NOTIFICATION_CACHE", key = "#id")
    public NotificationResponse getNotificationsById(String id) {
        return notificationMapper.toResponse(findNotificationByIdOrThrowError(id));
    }

    @Override
    @CacheEvict(value = {"NOTIFICATION_CACHE", "NOTIFICATION_PAGE_CACHE"}, key = "#id", allEntries = true)
    public void deleteNotification(String id) {
        Notification notification = findNotificationByIdOrThrowError(id);

        boolean haveAccess = authUtilService.isAdminOrOwner(notification.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.NOTIFICATION_DELETION_NOT_ALLOWED);
        }

        notification.setDeleted(true);
        notificationRepository.save(notification);
    }

    @Override
    @CacheEvict(value = "NOTIFICATION_PAGE_CACHE", allEntries = true)
    public void deleteAllMyNotification() {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        List<Notification> notifications = notificationRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId());
        if (notifications.isEmpty()) {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }

        boolean haveAccess = authUtilService.isAdminOrOwner(notifications.getFirst().getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.NOTIFICATION_DELETION_NOT_ALLOWED);
        }

        notifications.forEach(notification -> notification.setDeleted(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public Notification findNotificationByIdOrThrowError(String id) {
        return notificationRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

}
