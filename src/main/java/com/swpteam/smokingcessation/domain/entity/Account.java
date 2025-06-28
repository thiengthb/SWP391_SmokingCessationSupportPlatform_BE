package com.swpteam.smokingcessation.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swpteam.smokingcessation.common.AuditableEntity;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.enums.AuthProvider;
import com.swpteam.smokingcessation.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    AuthProvider provider;

    String providerId;

    @Column(unique = true, columnDefinition = "NVARCHAR(30)")
    String username;

    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(30)")
    String email;

    @Column(columnDefinition = "NVARCHAR(100)")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    @Column(unique = true, columnDefinition = "NVARCHAR(10)")
    String phoneNumber;

    @Enumerated(EnumType.STRING)
    Role role;

    @Enumerated(EnumType.STRING)
    AccountStatus status;

    String avatar;

    @JsonIgnore
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    Member member;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<AIUsage> aiUsages = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Blog> blogs = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Booking> bookingsAsMember = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Booking> bookingsAsCoach = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    Coach coach;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Notification> notifications = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Plan> plans = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<RecordHabit> recordHabits = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    Setting setting;

    @JsonIgnore
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    Score score;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Subscription> subscriptions = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Transaction> transactions = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TimeTable> timetables = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Streak> streaks = new ArrayList<>();

    @JsonIgnore
    public boolean isHavingSubscription() {
        return subscriptions != null &&
                subscriptions.stream().anyMatch(Subscription::isActive);
    }
}