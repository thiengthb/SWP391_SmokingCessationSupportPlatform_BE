package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.swpteam.smokingcessation.common.AuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class TimeTable extends AuditableEntity {

    String name;
    String description;
    LocalDateTime startedAt;
    LocalDateTime endedAt;

    @ManyToOne
    @JoinColumn(name = "coachId", nullable = false)
    @JsonBackReference
    Account coach;

    @OneToOne
    @JoinColumn(name = "booking_id", unique = true)
    Booking booking;
}
