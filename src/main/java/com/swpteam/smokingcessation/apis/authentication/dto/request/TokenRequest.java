package com.swpteam.smokingcessation.apis.authentication.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
<<<<<<<< HEAD:src/main/java/com/swpteam/smokingcessation/apis/authentication/dto/request/TokenRequest.java
public class TokenRequest {
    String token;
========
public class TokenRefreshRequest {
    @NotBlank(message = "REFRESH_TOKEN_REQUIRED")
    String refreshToken;
>>>>>>>> db0efc0 (feat(valid): add validations for requests, move refresh token to response header):src/main/java/com/swpteam/smokingcessation/apis/authentication/dto/request/TokenRefreshRequest.java
}
