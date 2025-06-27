package com.swpteam.smokingcessation.domain.dto.feedback;

import com.swpteam.smokingcessation.domain.enums.FeedbackType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {

    String id;
    String accountId;
    String comment;
    int rating;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    FeedbackType feedbackType;
}
