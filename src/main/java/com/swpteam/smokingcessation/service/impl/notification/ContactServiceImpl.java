package com.swpteam.smokingcessation.service.impl.notification;

import com.swpteam.smokingcessation.domain.dto.contact.ContactRequest;
import com.swpteam.smokingcessation.domain.entity.Contact;
import com.swpteam.smokingcessation.integration.mail.IMailService;
import com.swpteam.smokingcessation.repository.ContactRepository;
import com.swpteam.smokingcessation.service.interfaces.notification.IContactService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContactServiceImpl implements IContactService {
    ContactRepository contactRepository;
    IMailService mailService;

    @Transactional
    @PreAuthorize("hasRole('MEMBER')")
    @Override
    public void processContactRequest(ContactRequest request) {
        Contact contact = Contact.builder()
                .name(request.name())
                .email(request.email())
                .subject(request.subject())
                .content(request.content())
                .build();
        contactRepository.save(contact);

        // Gửi mail cho hệ thống/admin
        mailService.sendContactMail(request);

        log.info("Contact message saved and email sent from {}", request.email());

    }
}
