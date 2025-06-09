package com.swpteam.smokingcessation.apis.health.repository;

import com.swpteam.smokingcessation.apis.health.entity.Health;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HealthRepository extends JpaRepository<Health, UUID> {

    List<Health> findByAccount_Email(String email);

    boolean existsByIdAndAccount_Email(UUID healthId, String email);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.id = :accountId AND a.email = :email AND a.isDeleted = false")
    boolean existsAccountByIdAndEmail(@Param("accountId") String accountId, @Param("email") String email);
}