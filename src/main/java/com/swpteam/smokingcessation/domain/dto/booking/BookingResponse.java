package com.swpteam.smokingcessation.domain.dto.booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swpteam.smokingcessation.domain.enums.BookingStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {

    String id;
    String memberId;
    String memberName;
    String coachId;
    String coachFullName;
    String meetLink;
    LocalDateTime startedAt;
    LocalDateTime endedAt;
    BookingStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String declineReason;
}