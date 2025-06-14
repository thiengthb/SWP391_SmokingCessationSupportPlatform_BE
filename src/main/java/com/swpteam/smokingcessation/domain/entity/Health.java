package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Health extends BaseEntity {

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "accountId", nullable = false)
    Account account;

    int cigarettesPerDay;
    int cigarettesPerPack;
    int fndLevel;
    double packPrice;
    String reasonToQuit;
    int smokeYear;
}