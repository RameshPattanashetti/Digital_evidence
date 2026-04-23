package com.digitalevidence.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.digitalevidence.exception.ValidationException;

public class DigitalEvidence implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String evidenceId;
    private final String caseId;
    private final String description;
    private final EvidenceType type;
    private final String collectedBy;
    private final LocalDateTime collectedAt;
    private String currentCustodian;
    private EvidenceStatus status;
    private final List<CustodyEvent> custodyHistory = new ArrayList<>();

    public DigitalEvidence(String evidenceId, String caseId, String description, EvidenceType type, String collectedBy) {
        if (isBlank(evidenceId) || isBlank(caseId) || isBlank(description) || type == null || isBlank(collectedBy)) {
            throw new ValidationException("All evidence details are required");
        }
        this.evidenceId = evidenceId.trim();
        this.caseId = caseId.trim();
        this.description = description.trim();
        this.type = type;
        this.collectedBy = collectedBy.trim();
        this.collectedAt = LocalDateTime.now();
        this.currentCustodian = this.collectedBy;
        this.status = EvidenceStatus.IN_CUSTODY;
        custodyHistory.add(CustodyEvent.create("SYSTEM", this.collectedBy, "Initial collection"));
    }

    public String getEvidenceId() {
        return evidenceId;
    }

    public String getCaseId() {
        return caseId;
    }

    public String getDescription() {
        return description;
    }

    public EvidenceType getType() {
        return type;
    }

    public String getCollectedBy() {
        return collectedBy;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public String getCurrentCustodian() {
        return currentCustodian;
    }

    public EvidenceStatus getStatus() {
        return status;
    }

    public List<CustodyEvent> getCustodyHistory() {
        return Collections.unmodifiableList(custodyHistory);
    }

    public void transferCustody(String newCustodian, String reason) {
        if (isBlank(newCustodian)) {
            throw new ValidationException("New custodian is required");
        }
        if (isBlank(reason)) {
            throw new ValidationException("Transfer reason is required");
        }
        CustodyEvent event = CustodyEvent.create(currentCustodian, newCustodian.trim(), reason);
        custodyHistory.add(event);
        currentCustodian = newCustodian.trim();
        status = EvidenceStatus.TRANSFERRED;
    }

    public void archive() {
        status = EvidenceStatus.ARCHIVED;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
