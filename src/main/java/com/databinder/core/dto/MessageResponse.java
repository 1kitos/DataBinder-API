package com.databinder.core.dto;

import java.time.Instant;

import com.databinder.core.enums.MessageStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponse {

    private Long id;
    private String header;
    private String body;
    private Instant createdAt;
    private boolean read;
    private MessageStatus status;
}
