package com.databinder.core.dto.request;

public record UserCreateRequest(String username, String email, String phoneNumber) {}