package com.swpteam.smokingcessation.domain.dto.health;

import jakarta.validation.constraints.*;

public record HealthRequest(

    @Min(value = 0, message = "CIGARETTES_PER_DAY_INVALID")
    Integer cigarettesPerDay,

    @Min(value = 0, message = "CIGARETTES_PER_PACK_INVALID")
    Integer cigarettesPerPack,

    @DecimalMin(value = "0.0", message = "PACK_PRICE_INVALID")
    @DecimalMax(value = "500.0", message = "PACK_PRICE_TOO_HIGH")
    Double packPrice,

    @Min(value = 0, message = "FTND_LEVEL_INVALID_MIN")
    @Max(value = 10, message = "FTND_LEVEL_INVALID_MAX")
    Integer ftndLevel,

    @NotBlank(message = "REASON_TO_QUIT_REQUIRED")
    @Size(max = 255, message = "REASON_TO_QUIT_TOO_LONG")
    String reasonToQuit,

    @Min(value = 0, message = "SMOKE_YEAR_INVALID")
    @Max(value = 100, message = "SMOKE_YEAR_TOO_HIGH")
    Integer smokeYear
) {}
