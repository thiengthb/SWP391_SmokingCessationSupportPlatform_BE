package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.subscription.Subscription;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Membership extends BaseEntity {
    @Column(unique = true, nullable = false)
    String name;

    int duration;
    double price;
    String description;

    @OneToMany(mappedBy = "membership")
    List<Subscription> subscriptions;
}
