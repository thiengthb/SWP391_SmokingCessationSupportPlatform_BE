package com.swpteam.smokingcessation.domain.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDistributionResponse {
    int totalAccounts;
    int onlineAccounts;
    int offlineAccounts;
    int inactiveAccounts;
}
