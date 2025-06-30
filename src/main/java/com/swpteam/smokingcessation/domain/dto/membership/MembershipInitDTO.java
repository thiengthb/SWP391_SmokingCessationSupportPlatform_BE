package com.swpteam.smokingcessation.domain.dto.membership;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MembershipInitDTO {
    String name;
    String price;
    String currency;
    int durationDays;
    boolean highlighted;
    String description;
}
