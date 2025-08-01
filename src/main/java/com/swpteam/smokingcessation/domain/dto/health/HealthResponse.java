package com.swpteam.smokingcessation.domain.dto.health;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.enums.Currency;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthResponse {
    String id;
    String accountId;
    String ftndAnswers;
    int ftndLevel;
    int cigarettesPerDay;
    int cigarettesPerPack;
    double packPrice;
    Currency currency;
    String reasonToQuit;
    int smokeYear;
    LocalDateTime createdAt;
}