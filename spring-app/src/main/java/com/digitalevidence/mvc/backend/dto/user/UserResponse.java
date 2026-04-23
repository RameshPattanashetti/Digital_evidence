package com.digitalevidence.mvc.backend.dto.user;

import com.digitalevidence.mvc.backend.entity.UserRole;

public record UserResponse(Long id, String name, String email, UserRole role) {
}
