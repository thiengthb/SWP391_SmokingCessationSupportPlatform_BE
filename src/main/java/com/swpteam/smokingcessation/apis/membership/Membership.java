package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.subscription.Subscription;
import com.swpteam.smokingcessation.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Membership extends BaseEntity {

    String name;

    int durationDays;
    double price;
    String description;

    @OneToMany(mappedBy = "membership")
    List<Subscription> subscriptions;
}
