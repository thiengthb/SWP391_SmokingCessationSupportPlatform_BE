package com.swpteam.smokingcessation.feature.version1.profile.service.impl;

import com.swpteam.smokingcessation.domain.dto.account.AccountUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberCreateRequest;
import com.swpteam.smokingcessation.domain.dto.member.MemberProfileResponse;
import com.swpteam.smokingcessation.domain.dto.member.MemberUpdateRequest;
import com.swpteam.smokingcessation.domain.dto.member.ProgressResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.mapper.MemberMapper;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Member;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
import com.swpteam.smokingcessation.repository.jpa.MemberRepository;
import com.swpteam.smokingcessation.feature.version1.profile.service.IMemberService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberServiceImpl implements IMemberService {

    MemberMapper memberMapper;
    MemberRepository memberRepository;
    AuthUtilService authUtilService;
    IAccountService accountService;

    @Override
    public MemberProfileResponse getMyMemberProfile() {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        return memberMapper.toResponse(findMemberByIdOrThrowError(currentAccount.getId()));
    }

    @Override
    public MemberProfileResponse getMemberById(String accountId) {
        return memberMapper.toResponse(findMemberByIdOrThrowError(accountId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    public MemberProfileResponse createMember(MemberCreateRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        if (memberRepository.existsById(currentAccount.getId())) {
            throw new AppException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        Member member = memberMapper.toEntity(request);
        member.setAccount(currentAccount);

        return memberMapper.toResponse(memberRepository.save(member));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    public MemberProfileResponse updateMyMemberProfile(MemberUpdateRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Member member = findMemberByIdOrThrowError(currentAccount.getId());

        memberMapper.updateMember(member, request);

        accountService.updateAccountWithoutRole(member.getAccount().getId(),
                new AccountUpdateRequest(
                        request.email(),
                        null,
                        request.phoneNumber()
                )
        );

        return memberMapper.toResponse(memberRepository.save(member));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MemberProfileResponse updateMemberById(String accountId, MemberUpdateRequest request) {
        Member member = findMemberByIdOrThrowError(accountId);

        memberMapper.updateMember(member, request);

        accountService.updateAccountWithoutRole(member.getAccount().getId(),
                new AccountUpdateRequest(
                        request.email(),
                        null,
                        request.phoneNumber()
                )
        );

        return memberMapper.toResponse(memberRepository.save(member));
    }

    @Override
    public Member findMemberByIdOrThrowError(String accountId) {
        Member member = memberRepository.findByIdAndIsDeletedFalse(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        if (member.getAccount().isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return member;
    }

    @Override
    public List<Member> findAllMember() {
        List<Member> members = memberRepository.findAllByIsDeletedFalse();
        if (members.isEmpty()) {
            log.warn("no member found");
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }
        log.info("found {} member", members.size());
        return members;
    }

    @Override
    public ProgressResponse getProgress(){
        Member member = authUtilService.getCurrentAccountOrThrowError().getMember();

        return ProgressResponse.builder()
                .cigarettesAvoided((int) Math.floor(member.getCigarettesAvoided()))
                .moneySaved((int) Math.floor(member.getMoneySaved()))
                .build();
    }

}
