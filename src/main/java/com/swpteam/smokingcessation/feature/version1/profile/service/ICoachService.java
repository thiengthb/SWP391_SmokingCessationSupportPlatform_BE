package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachCreateRequest;
import com.swpteam.smokingcessation.domain.dto.coach.CoachResponse;
import com.swpteam.smokingcessation.domain.dto.coach.CoachUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Coach;

public interface ICoachService {

    PageResponse<CoachResponse> getCoachPage(PageableRequest request);

    CoachResponse getCoachById(String id);

    CoachResponse getMyCoachProfile();

    CoachResponse registerCoachProfile(CoachCreateRequest request);

    CoachResponse updateCoachById(String id, CoachUpdateRequest request);

    CoachResponse updateMyCoachProfile(CoachUpdateRequest request);

    Coach findCoachById(String id);

    PageResponse<CoachResponse> searchCoachesByName(String name, PageableRequest request);


}