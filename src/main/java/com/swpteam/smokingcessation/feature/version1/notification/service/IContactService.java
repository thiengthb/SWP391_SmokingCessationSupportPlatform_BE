package com.swpteam.smokingcessation.feature.version1.notification.service;

import com.swpteam.smokingcessation.domain.dto.contact.ContactRequest;

public interface IContactService {
    void processContactRequest(ContactRequest request);
}
