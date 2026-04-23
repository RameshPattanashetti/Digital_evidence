package com.digitalevidence.mvc.mongodb.dto.custody;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record CustodyRecordRequest(
        @NotBlank(message = "Evidence ID is required")
        String evidenceId,

        @NotBlank(message = "From person is required")
        String fromPerson,

        @NotBlank(message = "To person is required")
        String toPerson,

        LocalDateTime timestamp,

        @NotBlank(message = "Remarks are required")
        String remarks) {
}
