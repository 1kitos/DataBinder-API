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
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Manage messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public List<MessageResponse> getMyMessages() {
        return messageService.getMyMessages();
    }

    // USAR NO SWAGGER, NAO NO WEB
    @GetMapping("/user/{userId}")
    public List<MessageResponse> getMessagesForUser(
            @PathVariable Long userId) {

        return messageService.getMessagesForUser(userId);
    }
    // USAR NO SWAGGER, NAO NO WEB
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
    
    @PatchMapping("/{messageId}")
    public MessageResponse updateMessage(
            @PathVariable Long messageId,
            @RequestParam MessageStatus status,
            @RequestParam(required = false) Boolean read) {
        return messageService.updateMessage(messageId, status, read);
    }
}