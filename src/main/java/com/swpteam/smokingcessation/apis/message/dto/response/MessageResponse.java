package com.swpteam.smokingcessation.apis.message.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swpteam.smokingcessation.apis.message.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class MessageResponse {
    String messageId;
    MessageType type;
    String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
    boolean isDeleted;

}
