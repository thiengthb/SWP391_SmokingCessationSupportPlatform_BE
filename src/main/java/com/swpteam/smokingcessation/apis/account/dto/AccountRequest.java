package com.swpteam.smokingcessation.apis.account.dto;

import com.swpteam.smokingcessation.apis.account.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
<<<<<<<< HEAD:src/main/java/com/swpteam/smokingcessation/apis/account/dto/AccountRequest.java
public class AccountRequest {
========
public class AccountUpdateRequest {
>>>>>>>> 5495538dc181610a05051bfdc8404757085a59d5:src/main/java/com/swpteam/smokingcessation/apis/account/dto/AccountUpdateRequest.java
    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    String email;

<<<<<<<< HEAD:src/main/java/com/swpteam/smokingcessation/apis/account/dto/AccountRequest.java
    @NotBlank(message = "MESSAGE_REQUIRED")
========
    @NotBlank(message = "PASSWORD_REQUIRED")
>>>>>>>> 5495538dc181610a05051bfdc8404757085a59d5:src/main/java/com/swpteam/smokingcessation/apis/account/dto/AccountUpdateRequest.java
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;

    @Pattern(regexp = "\\d{10}", message = "PHONE_NUMBER_INVALID")
    String phoneNumber;
    Role role;
}