package com.swpteam.smokingcessation.apis.health;

import com.swpteam.smokingcessation.apis.account.Account;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "health")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Health {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    int cigarettesPerDay;
    int cigarettesPerPack;
    int fndLevel;
    double packPrice;
    String reasonToQuit;
    int smokeYear;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    Account account;
}