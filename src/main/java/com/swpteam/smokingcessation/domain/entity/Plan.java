package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
    Account account;

    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Phase> phases = new ArrayList<>();

    String planName;
    String description;
    LocalDate startDate;
    LocalDate endDate;

    long totalDaysReported;
    long totalDaysNotReported;
    int totalMostSmoked;
    int totalLeastSmoked;

    double successRate;
    double progress;

    @Enumerated(EnumType.STRING)
    PlanStatus planStatus;
}
