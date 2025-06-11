package com.swpteam.smokingcessation.apis.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.apis.account.enums.AccountStatus;
import com.swpteam.smokingcessation.apis.account.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {
    String id;
    String email;
    String phoneNumber;
    Role role;
    AccountStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
