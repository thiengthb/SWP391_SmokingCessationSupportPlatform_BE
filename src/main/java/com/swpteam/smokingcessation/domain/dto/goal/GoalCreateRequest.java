package com.swpteam.smokingcessation.domain.dto.goal;

import com.swpteam.smokingcessation.domain.enums.CriteriaType;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

public record GoalCreateRequest(

    @NotBlank(message = "NAME_REQUIRED")
    String name,

    @NotBlank(message = "DESCRIPTION_REQUIRED")
    String description,

    @URL(message = "ICON_URL_REQUIRED")
    @NotBlank(message = "ICON_URL_REQUIRED")
    String iconUrl,

    @NotNull(message = "CRITERIA_TYPE_REQUIRED")
    CriteriaType criteriaType,

    @NotNull(message = "CRITERIA_VALUE_REQUIRED")
    @Min(value = 0, message = "CRITERIA_VALUE_INVALID")
    Integer criteriaValue
){}
