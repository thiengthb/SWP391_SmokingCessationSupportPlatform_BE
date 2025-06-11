package com.swpteam.smokingcessation.apis.message;

import com.swpteam.smokingcessation.apis.message.dto.MessageRequest;
import com.swpteam.smokingcessation.apis.message.dto.MessageResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageService {
    MessageRepository messageRepository;
    MessageMapper messageMapper;

    public MessageResponse createMessage(MessageRequest request) {
        Message message = messageMapper.toMessage(request);
        message.setCreatedAt(LocalDateTime.now());
        Message saved = messageRepository.save(message);
        return messageMapper.toMessageResponse(saved);
    }

    public MessageResponse updateMessage(String id, MessageRequest request) {
        Message existing = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found with ID: " + id));

        existing.setContent(request.getContent());
        existing.setUpdatedAt(LocalDateTime.now());

        Message updated = messageRepository.save(existing);
        return messageMapper.toMessageResponse(updated);
    }


    public List<MessageResponse> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return messageMapper.toListMessageResponse(messages);
    }

    public void deleteMessage(String id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setDeleted(true);
        message.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(message);
    }


    public MessageResponse searchById(String id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));
        return messageMapper.toMessageResponse(message);

    }


}
