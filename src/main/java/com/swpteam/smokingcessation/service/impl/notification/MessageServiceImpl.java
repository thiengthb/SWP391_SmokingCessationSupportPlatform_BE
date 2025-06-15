package com.swpteam.smokingcessation.service.impl.notification;

import com.swpteam.smokingcessation.domain.dto.message.MessageRequest;
import com.swpteam.smokingcessation.domain.dto.message.MessageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Message;
import com.swpteam.smokingcessation.domain.mapper.MessageMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.MessageRepository;
import com.swpteam.smokingcessation.service.interfaces.notification.IMessageService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {

    MessageRepository messageRepository;
    MessageMapper messageMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<MessageResponse> getMessagePage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Message.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Message> messages = messageRepository.findAllByIsDeletedFalse(pageable);

        return messages.map(messageMapper::toMessageResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public MessageResponse getById(String id) {
        Message message = messageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        return messageMapper.toMessageResponse(message);
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
        Message message = messageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        messageMapper.update(message, request);

        return messageMapper.toMessageResponse(messageRepository.save(message));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void softDeleteMessageById(String id) {
        Message message = messageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        message.setDeleted(true);
        messageRepository.save(message);
    }
}
