package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.AuditableEntity;
import com.swpteam.smokingcessation.domain.enums.CriteriaType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Goal extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "accountId")
    Account account;

    String name;

    String iconUrl;

    String description;

    @Enumerated(EnumType.STRING)
    CriteriaType criteriaType;

    int criteriaValue;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    List<GoalProgress> goalProgresses;
}
