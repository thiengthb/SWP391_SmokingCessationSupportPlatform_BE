package com.swpteam.smokingcessation.apis.member;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.account.AccountRepository;
import com.swpteam.smokingcessation.apis.member.dto.MemberCreateRequest;
import com.swpteam.smokingcessation.apis.member.dto.MemberResponse;
import com.swpteam.smokingcessation.apis.member.dto.MemberUpdateRequest;
import com.swpteam.smokingcessation.constants.ErrorCode;
import com.swpteam.smokingcessation.exception.AppException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberService {

    MemberMapper memberMapper;
    MemberRepository memberRepository;
    AccountRepository accountRepository;

    public MemberResponse createMember(MemberCreateRequest request, String id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED));


        if (memberRepository.existsByFullName(request.getFullName())) {
            throw new AppException(ErrorCode.MEMBER_EXISTED);
        }

        Member member = memberMapper.toMember(request);
        member.setAccount(account);

        return memberMapper.toMemberResponse(memberRepository.save(member));
    }


    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    public MemberResponse getMemberById(String id) {
        return memberMapper.toMemberResponse(findMemberById(id));
    }

    private Member findMemberById(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_EXISTED));
        if (member.getAccount().isDeleted()) {
            throw new AppException(ErrorCode.ACCOUNT_DELETED);
        }
        return member;
    }

    @Transactional
    public MemberResponse updateMember(MemberUpdateRequest request, String id) {
        Member member = findMemberById(id);

        memberMapper.updateMember(member, request);
        memberRepository.save(member);

        return memberMapper.toMemberResponse(member);
    }
}
