package com.swpteam.smokingcessation.apis.record.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordResponse {

    private String id;
    private String accountId;
    private int cigarettesSmoked;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}