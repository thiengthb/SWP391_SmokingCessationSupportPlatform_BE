package com.swpteam.smokingcessation.repository.jpa;

import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByIdAndIsDeletedFalse(String id);

    Optional<Account> findByUsernameAndIsDeletedFalse(String username);

    Optional<Account> findByEmailAndIsDeletedFalse(String email);

    Optional<Account> findByProviderAndProviderId(String provider, String providerId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Page<Account> findAllByIsDeletedFalse(Pageable pageable);

    Page<Account> findAll(Pageable pageable);

    List<Account> findAllByIsDeletedFalse();


    Page<Account> findByUsernameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable );

    List<Account> findAllByStatusAndIsDeletedFalse(AccountStatus accountStatus);

    @Query("SELECT a.email FROM Account a WHERE a.role = 'ADMIN' AND a.isDeleted = false")
    List<String> findAllAdminEmails();
}
