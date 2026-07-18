package com.databinder.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private Long expiresIn;
    private UserResponse user;

}
