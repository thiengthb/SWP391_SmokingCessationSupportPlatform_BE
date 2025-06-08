package com.swpteam.smokingcessation.apis.member.repository;

import com.swpteam.smokingcessation.apis.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByFullName(String fullName);
}
