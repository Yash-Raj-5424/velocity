package com.velocity.userservice.controllers;

import com.velocity.userservice.dto.UserRegisterdto;
import com.velocity.userservice.dto.UserResponse;
import com.velocity.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterdto request){
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String id){
        return ResponseEntity.ok(userService.existById(id));
    }
}
