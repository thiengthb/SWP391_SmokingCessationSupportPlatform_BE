package com.swpteam.smokingcessation.apis.subscription;

import com.swpteam.smokingcessation.apis.subscription.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    Optional<Subscription> findByAccountIdAndPaymentStatus(String accountId, PaymentStatus status);

    Optional<Subscription> findByIdAndIsDeletedFalse(String id);

    Page<Subscription> findAllByIsDeletedFalse(Pageable pageable);
}
