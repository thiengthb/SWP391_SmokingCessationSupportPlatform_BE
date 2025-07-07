package com.swpteam.smokingcessation.domain.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserActivityResponse {
    LocalDate date;
    int currentAccounts;
    int newAccounts;
    int activeAccounts;
}
