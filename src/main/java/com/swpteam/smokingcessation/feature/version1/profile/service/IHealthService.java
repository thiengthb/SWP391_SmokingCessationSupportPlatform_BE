package com.swpteam.smokingcessation.feature.version1.profile.service;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthListItemResponse;
import com.swpteam.smokingcessation.domain.dto.health.HealthRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.domain.entity.Health;

public interface IHealthService {

    PageResponse<HealthListItemResponse> getHealthPage(PageableRequest request);

    PageResponse<HealthListItemResponse> getMyHealthPage(PageableRequest request);

    PageResponse<HealthListItemResponse> getHealthPageByAccountId(String accountId, PageableRequest request);

    HealthResponse getHealthById(String id);

    HealthResponse getMyLastestHealth();

    boolean hasCompleteFTNDAssessment();

    HealthResponse createHealth(HealthRequest request);

    HealthResponse updateHealth(String id, HealthRequest request);

    void softDeleteHealthById(String id);

    Health findHealthByIdOrThrowError(String id);

    Health findLatestHealthByAccountIdOrThrowError(String accountId);

    Health findLatestHealthByAccountIdOrNull(String accountId);
}
