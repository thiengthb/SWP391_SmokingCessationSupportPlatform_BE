package com.swpteam.smokingcessation.service.impl.tracking;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.streak.StreakResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Member;
import com.swpteam.smokingcessation.domain.entity.Streak;
import com.swpteam.smokingcessation.domain.mapper.StreakMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.MemberRepository;
import com.swpteam.smokingcessation.repository.StreakRepository;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.service.interfaces.profile.IMemberService;
import com.swpteam.smokingcessation.service.interfaces.tracking.IStreakService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StreakServiceImpl implements IStreakService {

    StreakMapper streakMapper;
    StreakRepository streakRepository;
    IAccountService accountService;
    IMemberService memberService;
    MemberRepository memberRepository;
    AuthUtilService authUtilService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<StreakResponse> getStreakPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Streak.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        return new PageResponse<>(streakRepository.findAll(pageable).map(streakMapper::toResponse));
    }

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    public PageResponse<StreakResponse> getMyStreakPage(PageableRequest request) {
        ValidationUtil.checkFieldExist(Streak.class, request.sortBy());

        Account currentAccount = authUtilService.getCurrentAccountOrThrowError();

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Streak> streaks = streakRepository.findAllByAccountIdAndIsDeletedFalse(currentAccount.getId(), pageable);

        return new PageResponse<>(streaks.map(streakMapper::toResponse));
    }

    @Override
    public StreakResponse getStreakByAccountId(String accountId) {
        return streakMapper.toResponse(findStreakByAccountIdOrThrowError(accountId));
    }

    @Override
    @Transactional
    public Streak createStreak(String accountId, int number) {
        Account account = accountService.findAccountByIdOrThrowError(accountId);

        Streak streak = Streak.builder()
                .account(account)
                .number(number)
                .build();

        return streakRepository.save(streak);
    }

    @Override
    @Transactional
    public Streak updateStreak(String accountId, int number) {
        Streak streak = findStreakByAccountIdOrThrowError(accountId);

        if (number < streak.getNumber()) {
            throw new AppException(ErrorCode.STREAK_DOWN_GRADE);
        }

        streak.setNumber(number);

        return streakRepository.save(streak);
    }

    @Override
    @Transactional
    public void resetStreak(String accountId) {
        accountService.findAccountByIdOrThrowError(accountId);

        Streak streak = findStreakByAccountIdOrThrowError(accountId);

        Member member = memberService.findMemberByIdOrThrowError(accountId);

        if (member.getHighestStreak() < streak.getNumber()) {
            member.setHighestStreak(streak.getNumber());
            memberRepository.save(member);
        }

        streak.setDeleted(true);
        streakRepository.save(streak);
    }

    @Override
    @Transactional
    public Streak findStreakByAccountIdOrThrowError(String id) {
        Streak streak = streakRepository.findByAccountIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.STREAK_NOT_FOUND));

        if (streak.getAccount().isDeleted()) {
            streak.setDeleted(true);
            streakRepository.save(streak);
            throw new AppException(ErrorCode.STREAK_NOT_FOUND);
        }

        return streak;
    }

}
