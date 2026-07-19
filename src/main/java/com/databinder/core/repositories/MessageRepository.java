package com.databinder.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.databinder.core.entities.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByToUserId(Long userId);
    
    List<Message> findByToUserIdOrderByCreatedAtDesc(Long userId);

}