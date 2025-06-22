package com.swpteam.smokingcessation.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Score {

    @ManyToOne
    @MapsId
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
    Account account;

    int number;
}
