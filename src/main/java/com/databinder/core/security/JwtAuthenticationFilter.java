package com.databinder.core.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.databinder.core.entities.User;
import com.databinder.core.repositories.UserRepository;
import com.databinder.core.services.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            String token = authHeader.substring(7);

            if (!jwtService.isValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractUsername(token);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

            	User user = userRepository.findByEmailIgnoreCase(email)
            	        .orElse(null);

            	if (user != null &&
            	        SecurityContextHolder.getContext().getAuthentication() == null) {

            	    CustomUserDetails userDetails = new CustomUserDetails(user);

            	    UsernamePasswordAuthenticationToken authentication =
            	            new UsernamePasswordAuthenticationToken(
            	                    userDetails,
            	                    null,
            	                    userDetails.getAuthorities());

            	    authentication.setDetails(
            	            new WebAuthenticationDetailsSource().buildDetails(request));

            	    SecurityContextHolder.getContext().setAuthentication(authentication);
            	}

            }

        } catch (JwtException | IllegalArgumentException ignored) {
            // Token inválido ou expirado.
            // O pedido continua como utilizador não autenticado.
        }

        filterChain.doFilter(request, response);
    }
}