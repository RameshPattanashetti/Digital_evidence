package com.digitalevidence.mvc.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCaseRequest(
        @NotBlank String caseId,
        @NotBlank String caseTitle,
        @NotBlank String investigatorName
) {
}
