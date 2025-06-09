package com.swpteam.smokingcessation.apis.membership;

import com.swpteam.smokingcessation.apis.subscription.Subscription;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Membership {
    @Id
    String name;
    int duration;
    double price;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "membership")
    private List<Subscription> subscriptions;
}
