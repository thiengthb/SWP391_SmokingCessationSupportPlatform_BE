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
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Old password is required")
    String oldPassword;

    @Size(min = 8, message = "The password need to be at least 8 characters")
    @NotBlank(message = "New password is required")
    String newPassword;
}