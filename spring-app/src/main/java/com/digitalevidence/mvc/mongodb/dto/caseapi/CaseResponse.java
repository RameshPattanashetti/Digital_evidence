package com.digitalevidence.mvc.mongodb.dto.caseapi;

import java.time.LocalDateTime;

import com.digitalevidence.mvc.mongodb.model.CaseStatus;

public record CaseResponse(String id, String caseNumber, String title, String description, CaseStatus status, LocalDateTime createdDate) {
}
