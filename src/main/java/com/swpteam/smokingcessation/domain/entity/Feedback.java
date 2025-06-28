package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.AuditableEntity;
import com.swpteam.smokingcessation.domain.enums.FeedbackType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
    Account account;

    String comment;

    @Enumerated(EnumType.STRING)
    FeedbackType feedbackType;

    int rating;
}
