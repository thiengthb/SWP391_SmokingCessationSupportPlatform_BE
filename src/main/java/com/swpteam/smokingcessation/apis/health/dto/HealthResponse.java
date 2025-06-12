package com.swpteam.smokingcessation.apis.health.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {
    String accountId;
    int cigarettesPerDay;
    int cigarettesPerPack;
    int fndLevel;
    double packPrice;
    String reasonToQuit;
    int smokeYear;
    LocalDateTime updatedAt;
    LocalDateTime createdAt;
}