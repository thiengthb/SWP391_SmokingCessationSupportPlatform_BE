package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coach extends BaseEntity {

    @OneToOne
    @MapsId
    @JoinColumn(name = "accountId")
    Account account;

    String fullName;
    String bio;
    int experienceYears;
    String socialLinks;
    String specializations;
    String certificates;

    @OneToMany(mappedBy = "coach")
    List<Booking> bookings;
}
