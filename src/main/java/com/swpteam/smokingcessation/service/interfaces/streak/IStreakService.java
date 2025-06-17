package com.swpteam.smokingcessation.service.interfaces.streak;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.streak.StreakResponse;
import com.swpteam.smokingcessation.domain.dto.streak.StreakRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IStreakService {
    StreakResponse createStreak(String id, StreakRequest request);

    StreakResponse updateStreak(String id, StreakRequest request);

    void deleteStreak(String id);

    StreakResponse getStreakById(String id);

    Page<StreakResponse> getStreakPage(PageableRequest request);
}
