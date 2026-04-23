package com.digitalevidence.mvc.mongodb.dto.common;

import java.time.LocalDateTime;

public record ApiErrorResponse(LocalDateTime timestamp, int status, String error, String path) {
}
