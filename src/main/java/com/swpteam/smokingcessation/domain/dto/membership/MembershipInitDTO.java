package com.swpteam.smokingcessation.domain.dto.membership;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isHighlighted")
    boolean highlighted;
    String description;
}
