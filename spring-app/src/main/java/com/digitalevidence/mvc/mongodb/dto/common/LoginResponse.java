package com.digitalevidence.mvc.mongodb.dto.common;

import com.digitalevidence.mvc.mongodb.dto.user.UserResponse;

public record LoginResponse(String message, UserResponse user) {
}
