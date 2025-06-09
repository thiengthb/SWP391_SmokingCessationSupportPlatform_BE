package com.swpteam.smokingcessation.apis.record.DTO.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecordUpdate {
    @Min(value = 0, message = "RECORD_CIGARETTES_SMOKED_INVALID")
    int cigarettesSmoked;

    @NotNull(message = "RECORD_DATE_REQUIRED")
    @FutureOrPresent(message = "RECORD_DATE_INVALID")
    LocalDate date;
}