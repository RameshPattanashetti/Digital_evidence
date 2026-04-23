package com.digitalevidence.mvc.mongodb.dto.caseapi;

import com.digitalevidence.mvc.mongodb.model.CaseStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CaseRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Status is required")
        CaseStatus status) {
}
