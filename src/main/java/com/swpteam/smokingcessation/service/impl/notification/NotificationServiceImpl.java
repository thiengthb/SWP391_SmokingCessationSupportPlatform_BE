package com.swpteam.smokingcessation.service.impl.notification;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationRequest;
import com.swpteam.smokingcessation.domain.dto.notification.NotificationResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Notification;
import com.swpteam.smokingcessation.domain.mapper.NotificationMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.NotificationRepository;
import com.swpteam.smokingcessation.service.interfaces.notification.INotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements INotificationService {

    SimpMessagingTemplate simpMessagingTemplate;
    NotificationMapper notificationMapper;
    NotificationRepository notificationRepository;
    AccountRepository accountRepository;

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
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


        String destination = (request.getAccountId() == null)
                ? "topic/notifications/all"
                : "topic/notifications" + request.getAccountId();

        simpMessagingTemplate.convertAndSend(destination, response);

        return response;
    }
}
