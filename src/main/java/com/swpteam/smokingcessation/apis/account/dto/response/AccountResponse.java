package com.swpteam.smokingcessation.apis.account.dto.response;

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
public class AccountResponse {
    String id;
    String email;
    String phoneNumber;
    Role role;
    AccountStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean isDeleted;
}
