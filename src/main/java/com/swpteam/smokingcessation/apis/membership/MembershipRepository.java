package com.swpteam.smokingcessation.apis.membership;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, String> {
    boolean existsByName(String name);

    Optional<Membership> findByName(String name);

    Page<Membership> findAll(Pageable pageable);
}
