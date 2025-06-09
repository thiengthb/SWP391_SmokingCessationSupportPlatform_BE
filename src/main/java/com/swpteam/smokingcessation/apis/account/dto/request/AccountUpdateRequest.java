package com.swpteam.smokingcessation.apis.account.dto.request;

import com.swpteam.smokingcessation.apis.account.enums.Role;
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
public class AccountUpdateRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "The password need to be at least 8 characters")
    String password;

    String phoneNumber;
    Role role;
}