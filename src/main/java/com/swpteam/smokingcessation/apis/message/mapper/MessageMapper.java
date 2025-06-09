package com.swpteam.smokingcessation.apis.message.mapper;

import com.swpteam.smokingcessation.apis.message.dto.request.MessageRequest;
import com.swpteam.smokingcessation.apis.message.dto.response.MessageResponse;
import com.swpteam.smokingcessation.apis.message.entity.Message;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageResponse toMessageResponse(Message message);

    Message toMessage(MessageRequest request);

    List<MessageResponse> toListMessageResponse(List<Message> messages);
}
