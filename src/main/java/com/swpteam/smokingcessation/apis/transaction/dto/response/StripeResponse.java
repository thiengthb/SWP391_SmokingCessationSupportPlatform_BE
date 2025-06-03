package com.swpteam.smokingcessation.apis.transaction.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StripeResponse {
    String message;
    String sessionId;
    String sessionUrl;
}
