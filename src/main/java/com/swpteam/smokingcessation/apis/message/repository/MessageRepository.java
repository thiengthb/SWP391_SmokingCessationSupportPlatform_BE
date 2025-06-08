package com.swpteam.smokingcessation.apis.message.repository;

import com.swpteam.smokingcessation.apis.message.entity.Message;
import com.swpteam.smokingcessation.apis.message.enums.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MessageRepository extends JpaRepository<Message,String> {
    List<Message> findByTypeAndIsDeletedFalse(MessageType type);

}
