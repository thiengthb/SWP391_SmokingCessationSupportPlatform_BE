package com.swpteam.smokingcessation.domain.dto.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberStatisticResponse {
    double avgCigarettesPerDay;
    long daysTracked;
    long cigarettesAvoided;
    double moneySaved;
}
