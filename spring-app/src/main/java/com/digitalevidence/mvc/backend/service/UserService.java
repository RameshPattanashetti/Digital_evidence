package com.digitalevidence.mvc.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalevidence.mvc.backend.dto.common.LoginResponse;
import com.digitalevidence.mvc.backend.dto.user.LoginRequest;
import com.digitalevidence.mvc.backend.dto.user.RegisterUserRequest;
import com.digitalevidence.mvc.backend.dto.user.UserResponse;
import com.digitalevidence.mvc.backend.entity.User;
import com.digitalevidence.mvc.backend.exception.BadRequestException;
import com.digitalevidence.mvc.backend.exception.ResourceNotFoundException;
import com.digitalevidence.mvc.backend.repository.UserRepository;

@Service
@Profile("!mongo")
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered: " + request.email());
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        // Password is stored as plain text for student demo simplicity only.
        user.setPassword(request.password());
        user.setRole(request.role());

        User saved = userRepository.save(user);
        log.info("Registered user with id={} and email={}", saved.getId(), saved.getEmail());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.getPassword().equals(request.password())) {
            throw new BadRequestException("Invalid email or password");
        }

        log.info("User login successful for id={} email={}", user.getId(), user.getEmail());
        return new LoginResponse("Login successful", toResponse(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with id={}", id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
