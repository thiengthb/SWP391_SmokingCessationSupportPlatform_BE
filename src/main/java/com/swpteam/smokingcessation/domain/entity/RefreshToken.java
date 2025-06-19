package com.swpteam.smokingcessation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken {

    @Id
    String id;

    @OneToOne
    @JoinColumn(name = "accountId", updatable = false, nullable = false)
    Account account;

    Date expiryTime;
}