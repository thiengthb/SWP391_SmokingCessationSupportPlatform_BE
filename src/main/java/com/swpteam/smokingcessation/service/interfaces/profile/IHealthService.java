package com.swpteam.smokingcessation.service.interfaces.profile;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthCreateRequest;
import com.swpteam.smokingcessation.domain.dto.health.HealthResponse;
import com.swpteam.smokingcessation.domain.dto.health.HealthUpdateRequest;
import com.swpteam.smokingcessation.domain.entity.Health;
import org.springframework.data.domain.Page;

public interface IHealthService {

    PageResponse<HealthResponse> getHealthPage(PageableRequest request);

    PageResponse<HealthResponse> getMyHealthPage(PageableRequest request);

    HealthResponse getHealthById(String id);

    PageResponse<HealthResponse> getHealthPageByAccountId(String accountId, PageableRequest request);

    HealthResponse createHealth(HealthCreateRequest request);

    HealthResponse updateHealth(String id, HealthUpdateRequest request);

    void softDeleteHealthById(String id);

    Health findHealthByIdOrThrowError(String id);
}
