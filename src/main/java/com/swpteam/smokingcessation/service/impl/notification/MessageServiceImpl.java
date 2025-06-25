package com.swpteam.smokingcessation.service.impl.notification;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.message.MessageRequest;
import com.swpteam.smokingcessation.domain.dto.message.MessageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Message;
import com.swpteam.smokingcessation.domain.entity.Subscription;
import com.swpteam.smokingcessation.domain.mapper.MessageMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.MessageRepository;
import com.swpteam.smokingcessation.service.interfaces.notification.IMessageService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageServiceImpl implements IMessageService {

    MessageRepository messageRepository;
    MessageMapper messageMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<MessageResponse> getMessagePage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Subscription.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Message> messages = messageRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(messages.map(messageMapper::toMessageResponse));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse getById(String id) {
        return messageMapper.toMessageResponse(findMessageByIdOrThrowError(id));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse createMessage(MessageRequest request) {
        Message message = messageMapper.toMessage(request);

        return messageMapper.toMessageResponse(messageRepository.save(message));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse updateMessage(String id, MessageRequest request) {
        Message message = findMessageByIdOrThrowError(id);

        messageMapper.update(message, request);

        return messageMapper.toMessageResponse(messageRepository.save(message));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void softDeleteMessageById(String id) {
        Message message = findMessageByIdOrThrowError(id);

        message.setDeleted(true);

        messageRepository.save(message);
    }

    @Override
    public Message findMessageByIdOrThrowError(String id) {
        return messageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
    }

}
