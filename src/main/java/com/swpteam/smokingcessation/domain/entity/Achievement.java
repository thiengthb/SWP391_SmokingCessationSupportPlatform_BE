package com.swpteam.smokingcessation.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Achievement {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    Account account;

    @ManyToOne
    @JoinColumn(name = "goalId", nullable = false)
    Goal goal;

    LocalDateTime earnedAt;
}
