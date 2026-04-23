package com.digitalevidence.mvc.dto;

import java.time.LocalDateTime;

import com.digitalevidence.mvc.model.EvidenceStatus;
import com.digitalevidence.mvc.model.EvidenceType;

public record EvidenceDetailResponse(
        String evidenceId,
        String caseId,
        String description,
        EvidenceType type,
        String collectedBy,
        String currentCustodian,
        EvidenceStatus status,
        LocalDateTime collectedAt
) {
}
