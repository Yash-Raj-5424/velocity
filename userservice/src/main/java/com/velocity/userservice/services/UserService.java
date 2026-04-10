package com.velocity.userservice.services;


import com.velocity.userservice.dto.UserRegisterdto;
import com.velocity.userservice.dto.UserResponse;
import com.velocity.userservice.exception.ResourceNotFoundException;
import com.velocity.userservice.models.User;
import com.velocity.userservice.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private UserResponse mapToResponse(User savedUser) {
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setKeycloakId(savedUser.getKeycloakId());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());
        return response;
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }


    public UserResponse register(UserRegisterdto request) {

        if(userRepository.existsByEmail(request.getEmail())){
            User existingUser = userRepository.findByEmail(request.getEmail());
            return mapToResponse(existingUser);
        }

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .keycloakId(request.getKeycloakId())
                .password(request.getPassword())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return mapToResponse(userRepository.save(newUser));
    }


    public Boolean existById(String id) {
        log.info("Checking existence of user with ID: {}", id);
        return userRepository.existsByKeycloakId(id);
    }
}
