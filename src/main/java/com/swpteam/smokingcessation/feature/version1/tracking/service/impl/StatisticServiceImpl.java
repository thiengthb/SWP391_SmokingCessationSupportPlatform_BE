package com.swpteam.smokingcessation.feature.version1.tracking.service.impl;

import com.swpteam.smokingcessation.domain.dto.statistics.MemberStatisticResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.feature.version1.tracking.service.IStatisticService;
import com.swpteam.smokingcessation.repository.report.IStatisticRepository;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticServiceImpl implements IStatisticService {

    AuthUtilService authUtilService;
    IStatisticRepository statisticRepository;

    @Override
    @PreAuthorize("hasRole('MEMBER')")
    public MemberStatisticResponse getMemberStatistics(){
        Account account = authUtilService.getCurrentAccountOrThrowError();

        return statisticRepository.getMemberStatisticsByAccountId(account.getId());
    }
}
