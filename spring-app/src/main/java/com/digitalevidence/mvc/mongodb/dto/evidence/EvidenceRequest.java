package com.digitalevidence.mvc.mongodb.dto.evidence;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EvidenceRequest(
        @NotBlank(message = "Type is required")
        String type,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Date is required")
        LocalDateTime date,

        @NotBlank(message = "Location is required")
        String location,

        @NotBlank(message = "Case ID is required")
        String caseId) {
}
