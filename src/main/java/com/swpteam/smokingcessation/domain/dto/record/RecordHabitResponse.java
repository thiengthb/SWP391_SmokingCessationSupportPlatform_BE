package com.swpteam.smokingcessation.domain.dto.record;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordHabitResponse {

    String id;
    String accountId;
    int cigarettesSmoked;
    String note;
    LocalDate date;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}