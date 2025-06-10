package com.swpteam.smokingcessation.apis.health.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthRequest {

    @NotBlank(message = "ACCOUNT_REQUIRED")
    String accountId;

    @Min(value = 0, message = "CIGARETTES_PER_DAY_INVALID")
    int cigarettesPerDay;

    @Min(value = 0, message = "CIGARETTES_PER_PACK_INVALID")
    int cigarettesPerPack;

    @Min(value = 0, message = "FND_LEVEL_INVALID_MIN")
    @Max(value = 10, message = "FND_LEVEL_INVALID_MAX")
    int fndLevel;

    @DecimalMin(value = "0.0", message = "PACK_PRICE_INVALID")
    @DecimalMax(value = "500.0", message = "PACK_PRICE_TOO_HIGH")
    double packPrice;

    @NotBlank(message = "REASON_TO_QUIT_REQUIRED")
    @Size(max = 255, message = "REASON_TO_QUIT_TOO_LONG")
    String reasonToQuit;

    @Min(value = 100, message = "SMOKE_YEAR_INVALID")
    int smokeYear;
}
