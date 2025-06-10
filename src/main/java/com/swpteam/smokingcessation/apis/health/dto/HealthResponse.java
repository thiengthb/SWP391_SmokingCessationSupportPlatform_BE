package com.swpteam.smokingcessation.apis.health.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {
    private UUID id;
    private String accountId;
    private int cigarettesPerDay;
    private int cigarettesPerPack;
    private int fndLevel;
    private double packPrice;
    private String reasonToQuit;
    private int smokeYear;
}