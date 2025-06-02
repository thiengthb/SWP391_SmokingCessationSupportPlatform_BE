package com.swpteam.smokingcessation.apis.membership.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
}
