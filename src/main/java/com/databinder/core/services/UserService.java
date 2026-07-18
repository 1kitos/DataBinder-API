package com.databinder.core.services;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.databinder.core.dto.LoginResponse;
import com.databinder.core.dto.UserResponse;
import com.databinder.core.dto.request.UserCreateRequest;
import com.databinder.core.dto.request.UserLoginRequest;
import com.databinder.core.dto.request.UserRegisterRequest;
import com.databinder.core.entities.User;
import com.databinder.core.enums.Role;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.mapping.ResponseMapper;
import com.databinder.core.repositories.PriceSnapshotRepository;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.UserRepository;
import com.databinder.core.security.CustomUserDetails;
import com.databinder.scrapping.CardmarketScrapingService;
import com.databinder.scrapping.CardmarketUrlBuilder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{

    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return ResponseMapper.toResponse(user);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(ResponseMapper::toResponse).toList();
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    public UserResponse register(UserRegisterRequest request) {

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use.");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.USER);
        user.setEnabled(true);

        user = userRepository.save(user);

        return ResponseMapper.toResponse(user);
    }
    
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
    

}