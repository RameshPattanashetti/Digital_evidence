package com.digitalevidence.mvc.mongodb.dto.user;

import com.digitalevidence.mvc.mongodb.model.UserRole;

public record UserResponse(String id, String name, String email, UserRole role) {
}
