package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.swpteam.smokingcessation.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coach extends AuditableEntity {

    @MapsId
    @OneToOne
    @JoinColumn(name = "accountId", nullable = false, updatable = false)
    Account account;

    String fullName;
    String bio;
    int experienceYears;
    String socialLinks;
    String specializations;
    String certificates;
}
