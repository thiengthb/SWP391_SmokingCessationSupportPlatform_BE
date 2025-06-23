package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.AuditableEntity;
import jakarta.persistence.*;
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
public class Goal extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "accountId")
    Account account;

    String name;

    String iconUrl;

    String description;

    String criteriaType;

    int criteriaValue;
}
