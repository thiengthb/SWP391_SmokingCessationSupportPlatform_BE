package com.swpteam.smokingcessation.domain.dto.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminStatisticResponse {
    double totalRevenue;
    List<MembershipRevenue> revenueByMembership;
}
