package com.swpteam.smokingcessation.service.impl.notification;

import com.swpteam.smokingcessation.domain.dto.phase.PhaseResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import org.springframework.stereotype.Service;

@Service
public class PhaseSummaryService {

    public void SendPhaseSummary(PhaseResponse phaseResponse, Account account) {
        String userMail = account.getEmail();

    }
}
