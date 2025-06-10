package com.swpteam.smokingcessation.apis.record.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecordUpdate {
    @Min(value = 0, message = "RECORD_CIGARETTES_SMOKED_INVALID")
    int cigarettesSmoked;
}