package com.swpteam.smokingcessation.domain.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.enums.TransactionStatus;
import com.swpteam.smokingcessation.domain.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    String id;
    String accountId;
    double amount;
    TransactionStatus status;
    PaymentMethod method;
    LocalDateTime createAt;
}
