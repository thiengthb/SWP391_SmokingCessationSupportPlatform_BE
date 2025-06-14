package com.swpteam.smokingcessation.feature.service.interfaces.profile;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachResponse;
import org.springframework.data.domain.Page;

public interface CoachService {
    Page<CoachResponse> getCoachPage(PageableRequest request);

    CoachResponse getCoachById(String id);

    CoachResponse createCoach(CoachRequest request);

    CoachResponse updateCoachById(String id, CoachRequest request);

    void softDeleteCoachById(String id);
}