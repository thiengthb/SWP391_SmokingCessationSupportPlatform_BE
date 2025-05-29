package com.swpteam.smoking_cessation.apis.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

        private String accountId;
        private String username;
        private String email;
        private String phoneNumber;
        private String role;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;


}
