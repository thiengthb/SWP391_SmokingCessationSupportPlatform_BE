package com.swpteam.smokingcessation.service.interfaces.tracking;

import com.swpteam.smokingcessation.domain.dto.counter.CounterResponse;

public interface ICounterService {

    CounterResponse startCounter();

    CounterResponse getCounter();
}
