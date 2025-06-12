package com.swpteam.smokingcessation.apis.message.dto;

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
    @NotBlank(message="MESSAGE_CONTENT_REQUIRED")
    String content;
}
