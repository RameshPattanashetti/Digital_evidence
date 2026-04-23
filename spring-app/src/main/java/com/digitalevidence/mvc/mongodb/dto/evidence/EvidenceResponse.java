package com.digitalevidence.mvc.mongodb.dto.evidence;

import java.time.LocalDateTime;

public record EvidenceResponse(String id, String type, String description, LocalDateTime date, String location, String caseId) {
}
