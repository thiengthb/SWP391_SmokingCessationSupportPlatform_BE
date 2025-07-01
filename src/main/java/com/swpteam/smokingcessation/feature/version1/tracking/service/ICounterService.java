package com.swpteam.smokingcessation.feature.version1.tracking.service;

import com.swpteam.smokingcessation.domain.dto.counter.CounterResponse;

public interface ICounterService {

    CounterResponse startCounter();

    CounterResponse getCounter();
}
