package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.RefreshToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByAccountId(String accountId);

    boolean existsById(@NotNull String id);

    void deleteAllByExpiryTimeBefore(Date now);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.account.id = :accountId")
    void deleteByAccountId(@Param("accountId") String accountId);
}
