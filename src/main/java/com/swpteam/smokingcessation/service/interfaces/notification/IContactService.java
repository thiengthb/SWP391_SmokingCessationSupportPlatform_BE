package com.swpteam.smokingcessation.service.interfaces.notification;

import com.swpteam.smokingcessation.domain.dto.contact.ContactRequest;

public interface IContactService {
    void processContactRequest(ContactRequest request);
}
