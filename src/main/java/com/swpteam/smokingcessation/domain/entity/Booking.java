package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.AuditableEntity;
import com.swpteam.smokingcessation.domain.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false, updatable = false)
    Account member;

    @ManyToOne
    @JoinColumn(name = "coachId", nullable = false)
    Account coach;

    String meetLink;
    LocalDateTime startedAt;
    LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    BookingStatus status;
}