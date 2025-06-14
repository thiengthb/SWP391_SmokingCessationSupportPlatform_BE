package com.swpteam.smokingcessation.repository;


import com.swpteam.smokingcessation.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {

}
