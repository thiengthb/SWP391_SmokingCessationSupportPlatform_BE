package com.swpteam.smokingcessation.repository.report;

import com.swpteam.smokingcessation.domain.dto.statistics.AdminStatisticResponse;
import com.swpteam.smokingcessation.domain.dto.statistics.MemberStatisticResponse;

public interface IStatisticRepository {
    MemberStatisticResponse getMemberStatisticsByAccountId(String accountId);

    MemberStatisticResponse getCurrentMonthMemberStatistics(String accountId);

    AdminStatisticResponse getAdminStatistics();

    AdminStatisticResponse getCurrentMonthAdminStatistics();
}
