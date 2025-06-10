package com.swpteam.smokingcessation.apis.record;

import com.swpteam.smokingcessation.apis.account.Account;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "record", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date", "account_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    int cigarettesSmoked;

    @Column(unique = true)
    LocalDate date;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    Account account;
}