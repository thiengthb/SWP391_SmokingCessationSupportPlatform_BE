package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swpteam.smokingcessation.domain.enums.Currency;
import com.swpteam.smokingcessation.common.AuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Membership extends AuditableEntity {

    @Builder.Default
    @OneToMany(mappedBy = "membership")
    @JsonIgnore
    List<Subscription> subscriptions = new ArrayList<>();

    String name;
    double price;
    int durationDays;
    String description;
    boolean highlighted;

    @Enumerated(EnumType.STRING)
    Currency currency;
}
