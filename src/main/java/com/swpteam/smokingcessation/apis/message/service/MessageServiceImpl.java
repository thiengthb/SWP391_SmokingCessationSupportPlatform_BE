package com.swpteam.smokingcessation.apis.message.service;

import com.swpteam.smokingcessation.apis.message.dto.request.MessageRequest;
import com.swpteam.smokingcessation.apis.message.dto.response.MessageResponse;
import com.swpteam.smokingcessation.apis.message.entity.Message;
import com.swpteam.smokingcessation.apis.message.mapper.MessageMapper;
import com.swpteam.smokingcessation.apis.message.repository.MessageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    MessageRepository messageRepository;
    MessageMapper messageMapper;

    @Override
    public MessageResponse createMessage(MessageRequest request) {
        Message message = messageMapper.toMessage(request);
        message.setCreatedAt(LocalDateTime.now());
        Message saved = messageRepository.save(message);
        return messageMapper.toMessageResponse(saved);
    }

    @Override
    public MessageResponse updateMessage(String id, MessageRequest request) {
        Message existing = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found with ID: " + id));

        existing.setContent(request.getContent());
        existing.setUpdatedAt(LocalDateTime.now());

        Message updated = messageRepository.save(existing);
        return messageMapper.toMessageResponse(updated);
    }


    @Override
    public List<MessageResponse> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return messageMapper.toListMessageResponse(messages);
    }

    @Override
    public void deleteMessage(String id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setDeleted(true);
        message.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Override
    public MessageResponse searchById(String id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));
        return messageMapper.toMessageResponse(message);

    }


}
