package com.swpteam.smokingcessation.feature.version1.tracking.service.impl;

import com.swpteam.smokingcessation.domain.dto.counter.CounterResponse;
import com.swpteam.smokingcessation.domain.entity.Member;
import com.swpteam.smokingcessation.feature.version1.profile.service.IMemberService;
import com.swpteam.smokingcessation.feature.version1.tracking.service.ICounterService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CounterServiceImpl implements ICounterService {

    AuthUtilService authUtilService;
    IMemberService memberService;

    @PreAuthorize("hasRole('MEMBER')")
    @Override
    @Transactional
    public CounterResponse startCounter() {
        Member member = memberService
                .findMemberByIdOrThrowError(authUtilService.getCurrentAccountOrThrowError().getId());

        member.setLastCounterReset(LocalDateTime.now());

        return CounterResponse.builder()
                .lastCounterReset(member.getLastCounterReset())
                .build();
    }

    @PreAuthorize("hasRole('MEMBER')")
    @Override
    @Transactional
    public CounterResponse getCounter() {
        Member member = memberService
                .findMemberByIdOrThrowError(authUtilService.getCurrentAccountOrThrowError().getId());

        return CounterResponse.builder()
                .lastCounterReset(member.getLastCounterReset())
                .build();
    }
}
