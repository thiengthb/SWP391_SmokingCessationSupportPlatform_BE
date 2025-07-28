package com.swpteam.smokingcessation.repository.report;

import com.swpteam.smokingcessation.domain.dto.statistics.MemberStatisticResponse;

public interface IStatisticRepository {
    MemberStatisticResponse getMemberStatisticsByAccountId(String accountId);
}
