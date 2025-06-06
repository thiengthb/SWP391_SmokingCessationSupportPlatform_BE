package com.swpteam.smokingcessation.apis.member.service;

import com.swpteam.smokingcessation.apis.account.entity.Account;
import com.swpteam.smokingcessation.apis.account.repository.AccountRepository;
import com.swpteam.smokingcessation.apis.member.dto.request.MemberCreateRequest;
import com.swpteam.smokingcessation.apis.member.dto.response.MemberResponse;
import com.swpteam.smokingcessation.apis.member.entity.Member;
import com.swpteam.smokingcessation.apis.member.mapper.MemberMapper;
import com.swpteam.smokingcessation.apis.member.repository.MemberRepository;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberService {

    MemberMapper memberMapper;
    MemberRepository memberRepository;
    AccountRepository accountRepository;

    public MemberResponse createMember(String id, MemberCreateRequest request) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));


        if (memberRepository.existsByFullName(request.getFullName())) {
            throw new AppException(ErrorCode.MEMBER_EXISTED);
        }

        Member member = memberMapper.toMember(request);
        member.setAccount(account);

        return memberMapper.toMemberResponse(memberRepository.save(member));
    }

}
