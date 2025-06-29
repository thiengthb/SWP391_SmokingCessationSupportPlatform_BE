package com.swpteam.smokingcessation.service.impl.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.domain.dto.member.MemberRequest;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.mapper.MemberMapper;
import com.swpteam.smokingcessation.domain.dto.member.MemberResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Member;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.MemberRepository;
import com.swpteam.smokingcessation.service.interfaces.profile.IMemberService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public MemberResponse getMemberById(String accountId) {
        return memberMapper.toResponse(findMemberByIdOrThrowError(accountId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    public MemberResponse createMember(MemberRequest request) {
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
    public MemberResponse updateMyMemberProfile(MemberRequest request) {
        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Member member = findMemberByIdOrThrowError(currentAccount.getId());

        memberMapper.updateMember(member, request);

        return memberMapper.toResponse(memberRepository.save(member));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MemberResponse updateMemberById(String accountId, MemberRequest request) {
        Member member = findMemberByIdOrThrowError(accountId);

        memberMapper.updateMember(member, request);

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

}
