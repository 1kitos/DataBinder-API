package com.databinder.core.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.databinder.core.dto.MessageResponse;
import com.databinder.core.entities.Message;
import com.databinder.core.enums.MessageStatus;
import com.databinder.core.services.MessageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Manage messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/user/{userId}")
    public List<MessageResponse> getMessagesForUser(@PathVariable Long userId) {
        return messageService.getMessagesForUser(userId);
    }

    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createMessage(
            @PathVariable Long userId,
            @RequestParam String header,
            @RequestParam String body) {

        return messageService.createMessage(userId, header, body);
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
    }
    
    @PostMapping("/{messageId}")
    public MessageResponse updateMessage(@RequestParam Long messageId,
    									 @RequestParam MessageStatus status, 
    									 @RequestParam(required = false) Boolean read)
    {
    	return messageService.updateMessage(messageId, status, read);
    }
    
}