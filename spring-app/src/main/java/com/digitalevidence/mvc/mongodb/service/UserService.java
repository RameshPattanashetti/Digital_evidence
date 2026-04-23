package com.digitalevidence.mvc.mongodb.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.digitalevidence.mvc.mongodb.dto.common.LoginResponse;
import com.digitalevidence.mvc.mongodb.dto.user.LoginRequest;
import com.digitalevidence.mvc.mongodb.dto.user.RegisterUserRequest;
import com.digitalevidence.mvc.mongodb.dto.user.UserResponse;
import com.digitalevidence.mvc.mongodb.exception.BadRequestException;
import com.digitalevidence.mvc.mongodb.exception.ResourceNotFoundException;
import com.digitalevidence.mvc.mongodb.model.User;
import com.digitalevidence.mvc.mongodb.repository.UserRepository;

@Service
@Profile("mongo")
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new BadRequestException("Email already registered: " + request.email());
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        // Plain text password kept intentionally for beginner demo (no JWT/security stack).
        user.setPassword(request.password());
        user.setRole(request.role());

        User saved = userRepository.save(user);
        log.info("Mongo user registered with id={} email={}", saved.getId(), saved.getEmail());
        return toResponse(saved);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.getPassword().equals(request.password())) {
            throw new BadRequestException("Invalid email or password");
        }

        return new LoginResponse("Login successful", toResponse(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("Mongo user deleted with id={}", id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
