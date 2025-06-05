package com.swpteam.smokingcessation.apis.account.dto.request;

import com.swpteam.smokingcessation.apis.account.enums.AccountStatus;
import com.swpteam.smokingcessation.apis.account.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountCreateRequest {
    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "MESSAGE_REQUIRED")
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    @Pattern(regexp = "\\d{10}", message = "PHONE_NUMBER_INVALID")
    String phoneNumber;
    Role role;
    AccountStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean isDeleted;
}