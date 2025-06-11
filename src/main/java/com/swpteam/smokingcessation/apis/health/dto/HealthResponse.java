package com.swpteam.smokingcessation.apis.health.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

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