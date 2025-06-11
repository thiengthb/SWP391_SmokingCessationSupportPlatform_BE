package com.swpteam.smokingcessation.apis.subscription.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.apis.subscription.enums.PaymentStatus;
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

    String id;
    String accountId;
    String membershipName;
    PaymentStatus paymentStatus;
    LocalDate startDate;
    LocalDate endDate;
    LocalDate createdAt;
    LocalDate updatedAt;
}
