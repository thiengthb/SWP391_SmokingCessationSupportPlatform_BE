package com.swpteam.smokingcessation.domain.dto.goal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallOfFameResponse {
    AccountResponse account;
    LocalDateTime timestamp;
    String criteriaType;
    int criteriaValue;
    String description;
}
