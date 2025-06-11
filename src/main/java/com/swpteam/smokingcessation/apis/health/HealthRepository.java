package com.swpteam.smokingcessation.apis.health;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.membership.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthRepository extends JpaRepository<Health, String> {

    Page<Health> findByAccountIdAndIsDeletedFalse(String accountId, Pageable pageable);

    boolean existsByAccountIdAndIsDeletedFalse(String accountId);

    Optional<Health> findFirstByAccountIdAndIsDeletedFalse(String accountId);

    Page<Health> findAllByIsDeletedFalse(Pageable pageable);

    Optional<Health> findByIdAndIsDeletedFalse(String id);

    String account(Account account);
}
