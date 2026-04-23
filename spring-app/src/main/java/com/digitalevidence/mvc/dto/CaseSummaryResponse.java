package com.digitalevidence.mvc.dto;

public record CaseSummaryResponse(
        String caseId,
        String caseTitle,
        String investigatorName,
        long evidenceCount
) {
}
