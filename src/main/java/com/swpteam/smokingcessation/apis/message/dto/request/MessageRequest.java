package com.swpteam.smokingcessation.apis.message.dto.request;

import com.swpteam.smokingcessation.apis.message.enums.MessageType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRequest {
    @NotNull(message = "MESSAGE_TYPE_REQUIRED")
    MessageType type;
    @NotBlank(message="CONTENT_REQUIRED")
    String content;
    @Builder.Default
    boolean isDeleted=false;

}
