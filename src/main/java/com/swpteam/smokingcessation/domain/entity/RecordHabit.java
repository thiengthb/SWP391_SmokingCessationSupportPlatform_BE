package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecordHabit extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
    Account account;

    int cigarettesSmoked;

    String note;

    @Column(unique = true, nullable = false)
    LocalDate date;

    boolean progressed;
}