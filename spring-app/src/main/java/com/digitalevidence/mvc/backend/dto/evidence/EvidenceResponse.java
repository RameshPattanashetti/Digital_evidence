package com.digitalevidence.mvc.backend.dto.evidence;

import java.time.LocalDateTime;

public record EvidenceResponse(Long id, String type, String description, LocalDateTime date, String location, Long caseId) {
}
