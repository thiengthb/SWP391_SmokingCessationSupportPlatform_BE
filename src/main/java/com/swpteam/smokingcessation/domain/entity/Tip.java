package com.swpteam.smokingcessation.domain.entity;

import com.swpteam.smokingcessation.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @ManyToOne
    @JoinColumn(name = "phaseId", nullable = false, updatable = false)
    Phase phase;

    String content;

}
