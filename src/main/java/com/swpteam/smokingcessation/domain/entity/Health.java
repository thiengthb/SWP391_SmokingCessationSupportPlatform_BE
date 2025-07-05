package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.AuditableEntity;
import com.swpteam.smokingcessation.domain.enums.Currency;
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
public class Health extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
    Account account;

    @Lob
    @Column(columnDefinition = "TEXT")
    String ftndAnswers;

    int ftndLevel;
    int cigarettesPerDay;
    int cigarettesPerPack;
    double packPrice;

    @Enumerated(EnumType.STRING)
    Currency currency;

    String reasonToQuit;
    int smokeYear;
}