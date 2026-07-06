package com.databinder.core.dto;

import com.databinder.core.enums.MessageStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponse {

    private Long id;
    private Long userId;
    private String header;
    private String body;
    private MessageStatus status;
}
