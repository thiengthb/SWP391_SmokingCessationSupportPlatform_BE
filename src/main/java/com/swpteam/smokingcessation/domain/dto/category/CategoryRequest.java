package com.swpteam.smokingcessation.domain.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest (

    @NotBlank(message = "CATEGORY_NAME_REQUIRED")
    @Size(max = 100, message = "CATEGORY_NAME_TOO_LONG")
    String name
) {}
