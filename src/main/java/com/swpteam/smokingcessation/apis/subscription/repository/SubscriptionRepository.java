package com.swpteam.smokingcessation.apis.subscription.repository;

import com.swpteam.smokingcessation.apis.subscription.entity.PaymentStatus;
import com.swpteam.smokingcessation.apis.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Optional<Subscription> findByAccountIdAndPaymentStatus(String accountId, PaymentStatus status);
}
