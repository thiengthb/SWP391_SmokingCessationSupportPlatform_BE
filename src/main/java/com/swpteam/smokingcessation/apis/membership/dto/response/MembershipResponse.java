package com.swpteam.smokingcessation.apis.membership.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipResponse {
    String name;
    int duration;
    double price;
    String description;
}
