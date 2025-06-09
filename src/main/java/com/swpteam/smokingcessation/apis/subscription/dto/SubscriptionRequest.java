package com.swpteam.smokingcessation.apis.subscription.dto;

import com.swpteam.smokingcessation.apis.subscription.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionRequest {
    @Email(message = "INVALID_EMAIL_FORMAT")
    @NotBlank(message = "EMAIL_REQUIRED")
    String email;

    @NotNull(message = "MEMBERSHIP_REQUIRED")
    @Size(min = 1, message = "MEMBERSHIP_MIN_SIZE")
    String membershipName;

    @NotNull(message = "START_DATE_REQUIRED")
    @FutureOrPresent(message = "START_DATE_MUST_BE_TODAY_OR_FUTURE")
    LocalDate startDate;

    @NotNull(message = "END_DATE_REQUIRED")
    @Future(message = "END_DATE_MUST_BE_IN_FUTURE")
    LocalDate endDate;

    @NotNull(message = "PAYMENT_STATUS_REQUIRED")
    PaymentStatus paymentStatus;
}
