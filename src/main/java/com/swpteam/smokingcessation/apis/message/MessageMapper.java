package com.swpteam.smokingcessation.apis.message;

import com.swpteam.smokingcessation.apis.message.dto.MessageRequest;
import com.swpteam.smokingcessation.apis.message.dto.MessageResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageResponse toMessageResponse(Message message);
    Message toMessage(MessageRequest request);
    List<MessageResponse> toListMessageResponse(List<Message> messages);
}
