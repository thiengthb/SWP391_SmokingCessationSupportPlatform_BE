package com.swpteam.smokingcessation.domain.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {

    @JsonProperty("user")
    AccountResponse accountResponse;

    String accessToken;
    String refreshToken;
}
