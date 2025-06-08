package com.swpteam.smokingcessation.apis.subscription.dto.response;

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
public class SubscriptionResponse {
    String accountId;
    String membershipName;
    LocalDate startDate;
    LocalDate endDate;
}
