package com.databinder.core.dto;

import com.databinder.core.entities.User;
import com.databinder.core.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private Role role;
}
