package com.digitalevidence.mvc.backend.dto.common;

import com.digitalevidence.mvc.backend.dto.user.UserResponse;

public record LoginResponse(String message, UserResponse user) {
}
