package com.swpteam.smokingcessation.apis.membership.repository;

import com.swpteam.smokingcessation.apis.membership.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, String> {
    Optional<Membership> findByName(String name);
}
