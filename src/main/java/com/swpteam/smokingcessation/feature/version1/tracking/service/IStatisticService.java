package com.swpteam.smokingcessation.feature.version1.tracking.service;

import com.swpteam.smokingcessation.domain.dto.statistics.AdminStatisticResponse;
import com.swpteam.smokingcessation.domain.dto.statistics.MemberStatisticResponse;

public interface IStatisticService {
    MemberStatisticResponse getMemberStatistics();

    MemberStatisticResponse getCurrentMonthMemberStatistics();

    AdminStatisticResponse getAdminStatistics();
}
