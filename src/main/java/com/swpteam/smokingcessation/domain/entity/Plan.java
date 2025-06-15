package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.domain.enums.PlanStatus;
import com.swpteam.smokingcessation.common.BaseEntity;
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
public class Plan extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
    Account account;

    String planName;
    double successRate;
    String description;
    LocalDate startDate;
    LocalDate endDate;

    @Enumerated(EnumType.STRING)
    PlanStatus planStatus;

    @OneToMany(mappedBy = "plan")
    List<Phase> phases = new ArrayList<>();
}
