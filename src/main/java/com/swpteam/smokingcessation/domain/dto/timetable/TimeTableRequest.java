package com.swpteam.smokingcessation.domain.dto.timetable;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;


public record TimeTableRequest (
    @NotBlank(message = "TIMETABLE_NAME_REQUIRED")
    String name,

    @NotBlank(message = "TIMETABLE_DESCRIPTION_REQUIRED")
    String description,

    @NotNull(message = "TIMETABLE_STARTED_AT_REQUIRED")
    @FutureOrPresent(message = "TIMETABLE_STARTED_AT_INVALID")
    LocalDateTime startedAt,

    @NotNull(message = "TIMETABLE_ENDED_AT_REQUIRED")
    @Future(message = "TIMETABLE_ENDED_AT_INVALID")
    LocalDateTime endedAt
) {}




