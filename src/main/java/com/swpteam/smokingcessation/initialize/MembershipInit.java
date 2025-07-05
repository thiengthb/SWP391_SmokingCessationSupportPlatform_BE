package com.swpteam.smokingcessation.initialize;

import com.swpteam.smokingcessation.constant.ResourceFilePaths;
import com.swpteam.smokingcessation.domain.dto.membership.MembershipInitDTO;
import com.swpteam.smokingcessation.domain.entity.Membership;
import com.swpteam.smokingcessation.domain.enums.Currency;
import com.swpteam.smokingcessation.repository.jpa.MembershipRepository;
import com.swpteam.smokingcessation.feature.version1.internalization.MessageSourceService;
import com.swpteam.smokingcessation.utils.FileLoaderUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MembershipInit implements CommandLineRunner {

    MembershipRepository membershipRepository;
    MessageSourceService messageSourceService;

    @Override
    public void run(String... args) {
        if (membershipRepository.count() == 0) {
            List<MembershipInitDTO> membershipInits = FileLoaderUtil.loadMemberships(ResourceFilePaths.INIT_MEMBERSHIPS);
            for (MembershipInitDTO item : membershipInits) {
                String localizeName = messageSourceService.getLocalizeMessage(item.getName());
                String localizeCurrency = messageSourceService.getLocalizeMessage(item.getCurrency());
                String localizePrice = messageSourceService.getLocalizeMessage(item.getPrice());
                String localizeDescription = messageSourceService.getLocalizeMessage(item.getDescription());

                Membership membership = Membership.builder()
                        .name(localizeName)
                        .price(Double.parseDouble(localizePrice))
                        .currency(Currency.valueOf(localizeCurrency))
                        .durationDays(item.getDurationDays())
                        .highlighted(item.isHighlighted())
                        .description(localizeDescription)
                        .build();

                membershipRepository.save(membership);
                log.info("A default membership name {} has been created", localizeName);
            }
            log.info("Successfully created default memberships");
        }
    }

}
