package com.digitalevidence.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.digitalevidence.exception.ValidationException;

public class CaseFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String caseId;
    private final String caseTitle;
    private final String investigatorName;
    private final LocalDateTime createdAt;
    private final List<String> evidenceIds = new ArrayList<>();

    public CaseFile(String caseId, String caseTitle, String investigatorName) {
        if (isBlank(caseId) || isBlank(caseTitle) || isBlank(investigatorName)) {
            throw new ValidationException("Case id, title, and investigator name are required");
        }
        this.caseId = caseId.trim();
        this.caseTitle = caseTitle.trim();
        this.investigatorName = investigatorName.trim();
        this.createdAt = LocalDateTime.now();
    }

    public String getCaseId() {
        return caseId;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public String getInvestigatorName() {
        return investigatorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getEvidenceIds() {
        return Collections.unmodifiableList(evidenceIds);
    }

    public void addEvidenceId(String evidenceId) {
        if (isBlank(evidenceId)) {
            throw new ValidationException("Evidence id is required");
        }
        if (!evidenceIds.contains(evidenceId.trim())) {
            evidenceIds.add(evidenceId.trim());
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
