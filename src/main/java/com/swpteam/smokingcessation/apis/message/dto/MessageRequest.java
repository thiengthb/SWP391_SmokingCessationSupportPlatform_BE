package com.swpteam.smokingcessation.apis.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
<<<<<<<< HEAD:src/main/java/com/swpteam/smokingcessation/apis/message/dto/MessageRequest.java
public class MessageRequest {

    @NotBlank(message="MESSAGE_CONTENT_REQUIRED")
    String content;
========
public class RefreshTokenRequest {
    @NotBlank(message = "REFRESH_TOKEN_REQUIRED")
    String refreshToken;
>>>>>>>> b8e34c7 (fix(security): clean up excess file):src/main/java/com/swpteam/smokingcessation/apis/authentication/dto/request/RefreshTokenRequest.java
}
