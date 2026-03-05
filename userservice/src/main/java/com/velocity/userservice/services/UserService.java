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
        return response;
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }


    public UserResponse register(UserRegisterdto request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already in use");
        }
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);

    }


    public Boolean existById(String id) {
        log.info("Checking existence of user with ID: {}", id);
        return userRepository.existsById(id);
    }
}
