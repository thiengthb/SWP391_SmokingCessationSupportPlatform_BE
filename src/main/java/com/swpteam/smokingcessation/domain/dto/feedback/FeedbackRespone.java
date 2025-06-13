package com.swpteam.smokingcessation.domain.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRespone {
    private String id;
    private String accountId;
    private String comment;
    private String feedback;
    LocalDateTime createdAt;
}
