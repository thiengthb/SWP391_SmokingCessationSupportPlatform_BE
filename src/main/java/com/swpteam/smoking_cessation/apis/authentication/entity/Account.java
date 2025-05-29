package com.swpteam.smoking_cessation.apis.authentication.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(30)")
    String username;

    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    String password;

    @Column(nullable = false, unique = true,columnDefinition = "NVARCHAR(100)")
    String email;

    @Column(nullable = false, updatable = false)
    LocalDate createdAt;

    LocalDate updatedAt;

    @Column (nullable = false)
    boolean isDeleted;
}
