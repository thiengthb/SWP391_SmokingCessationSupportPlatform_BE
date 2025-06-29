package com.swpteam.smokingcessation.repository;

import com.swpteam.smokingcessation.domain.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, String> {
}
