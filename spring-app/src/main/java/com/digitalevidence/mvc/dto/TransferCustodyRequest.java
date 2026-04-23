package com.digitalevidence.mvc.dto;

import jakarta.validation.constraints.NotBlank;

public record TransferCustodyRequest(
        @NotBlank String evidenceId,
        @NotBlank String newCustodian,
        @NotBlank String reason
) {
}
