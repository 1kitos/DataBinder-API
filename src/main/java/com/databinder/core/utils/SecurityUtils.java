package com.databinder.core.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.databinder.core.entities.User;
import com.databinder.core.security.CustomUserDetails;

public final class SecurityUtils {

    private SecurityUtils() {}
    

    public static User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails user =
                (CustomUserDetails) authentication.getPrincipal();

        return user.getUser();
    }
}