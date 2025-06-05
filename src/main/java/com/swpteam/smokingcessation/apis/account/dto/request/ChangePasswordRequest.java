package com.swpteam.smokingcessation.apis.account.dto.request;

import com.swpteam.smokingcessation.apis.account.validation.DifferentPasswords;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@DifferentPasswords
public class ChangePasswordRequest {
    @NotBlank(message = "BLANK_INVALID")
    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "BLANK_INVALID")
    String oldPassword;

    @Size(min = 8, message = "PASSWORD_INVALID")
    @NotBlank(message = "BLANK_INVALID")
    String newPassword;
}
