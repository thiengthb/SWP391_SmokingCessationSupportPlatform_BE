package com.swpteam.smokingcessation.apis.mail;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailRequest {
    String accountId;
    String subscriptionId;
    double amount;
}
