package com.swpteam.smokingcessation.service.interfaces.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachResponse;
import com.swpteam.smokingcessation.domain.entity.Coach;
import org.springframework.data.domain.Page;

public interface ICoachService {

    PageResponse<CoachResponse> getCoachPage(PageableRequest request);

    CoachResponse getCoachById(String id);

    CoachResponse getMyCoachProfile();

    CoachResponse registerCoachProfile(CoachRequest request);

    CoachResponse updateCoachById(String id, CoachRequest request);

    CoachResponse updateMyCoachProfile(CoachRequest request);

    Coach findCoachById(String id);
    
}