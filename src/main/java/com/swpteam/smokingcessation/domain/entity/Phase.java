package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swpteam.smokingcessation.domain.enums.PhaseStatus;
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
public class Phase extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "planId", nullable = false, updatable = false)
    Plan plan;

    int phase;
    String phaseName;
    String description;
    int cigaretteBound;
    LocalDate startDate;
    LocalDate endDate;

    long totalDaysReported;
    long totalDaysNotReported;
    int mostSmokeCig;
    int leastSmokeCig;

    Double successRate;

    @Enumerated(EnumType.STRING)
    PhaseStatus phaseStatus;

    @Builder.Default
    @OneToMany(mappedBy = "phase", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Tip> tips = new ArrayList<>();

}
