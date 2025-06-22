package com.swpteam.smokingcessation.domain.entity;


import com.swpteam.smokingcessation.common.AuditableEntity;
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
public class Streak extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false)
    Account account;

    int number;
}
