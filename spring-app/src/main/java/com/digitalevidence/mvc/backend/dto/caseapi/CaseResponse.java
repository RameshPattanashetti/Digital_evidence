package com.digitalevidence.mvc.backend.dto.caseapi;

import java.time.LocalDateTime;

import com.digitalevidence.mvc.backend.entity.CaseStatus;

public record CaseResponse(Long id, String title, String description, CaseStatus status, LocalDateTime createdDate) {
}
