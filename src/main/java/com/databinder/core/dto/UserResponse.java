package com.databinder.core.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    Instant createdAt;
}
