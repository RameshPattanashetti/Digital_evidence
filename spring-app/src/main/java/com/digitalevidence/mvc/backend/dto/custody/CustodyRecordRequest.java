package com.digitalevidence.mvc.backend.dto.custody;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustodyRecordRequest(
        @NotNull(message = "Evidence ID is required")
        Long evidenceId,

        @NotBlank(message = "From person is required")
        String fromPerson,

        @NotBlank(message = "To person is required")
        String toPerson,

        LocalDateTime timestamp,

        @NotBlank(message = "Remarks are required")
        String remarks) {
}
