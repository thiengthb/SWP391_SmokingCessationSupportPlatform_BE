package com.swpteam.smokingcessation.apis.member.repository;

import com.swpteam.smokingcessation.apis.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository <Member,String> {
    Optional<Member> findByFullName(String fullName);
    boolean existsByFullName(String fullName);
}
