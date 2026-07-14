package com.databinder.core.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.databinder.core.dto.UserResponse;
import com.databinder.core.dto.request.UserCreateRequest;
import com.databinder.core.entities.User;
import com.databinder.core.exception.ResourceNotFoundException;
import com.databinder.core.mapping.ResponseMapper;
import com.databinder.core.repositories.PriceSnapshotRepository;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.UserRepository;
import com.databinder.scrapping.CardmarketScrapingService;
import com.databinder.scrapping.CardmarketUrlBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse create(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        return ResponseMapper.toResponse(userRepository.save(user));
    }

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


}