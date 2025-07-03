package com.swpteam.smokingcessation.feature.version1.tracking.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.streak.StreakResponse;
import com.swpteam.smokingcessation.domain.entity.Streak;

public interface IStreakService {

    Streak createStreak(String accountId, int number);

    Streak updateStreak(String accountId, int number);

    void resetStreak(String id);

    StreakResponse getStreakByAccountId(String accountId);

    PageResponse<StreakResponse> getMyStreakPage(PageableRequest request);

    PageResponse<StreakResponse> getStreakPage(PageableRequest request);

    Streak findStreakByAccountIdOrThrowError(String id);
    
}
