package com.digitalevidence.mvc.dto;

import com.digitalevidence.mvc.model.EvidenceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddEvidenceRequest(
        @NotBlank String caseId,
        @NotBlank String evidenceId,
        @NotBlank String description,
        @NotNull EvidenceType type,
        @NotBlank String collectedBy
) {
}
