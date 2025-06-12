package com.swpteam.smokingcessation.apis.message;

import com.swpteam.smokingcessation.apis.message.dto.MessageRequest;
import com.swpteam.smokingcessation.apis.message.dto.MessageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageService {
    MessageRepository messageRepository;
    MessageMapper messageMapper;

    public Page<MessageResponse> getMessagePage(PageableRequest request) {
        if (!ValidationUtil.isFieldExist(Message.class, request.getSortBy())) {
            throw new AppException(ErrorCode.INVALID_SORT_FIELD);
        }

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Message> messages = messageRepository.findAllByIsDeletedFalse(pageable);

        return messages.map(messageMapper::toMessageResponse);
    }

    public MessageResponse getById(String id) {
        Message message = messageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));
        return messageMapper.toMessageResponse(message);
    }

    @Transactional
    public MessageResponse createMessage(MessageRequest request) {
        Message message = messageMapper.toMessage(request);
        message.setCreatedAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);
        return messageMapper.toMessageResponse(saved);
    }

    @Transactional
    public MessageResponse updateMessage(String id, MessageRequest request) {
        Message message = messageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        message.setContent(request.getContent());
        message.setUpdatedAt(LocalDateTime.now());

        Message updated = messageRepository.save(message);
        return messageMapper.toMessageResponse(updated);
    }

    @Transactional
    public void softDeleteMessageById(String id) {
        Message message = messageRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_NOT_FOUND));

        message.setDeleted(true);
        messageRepository.save(message);
    }
}
