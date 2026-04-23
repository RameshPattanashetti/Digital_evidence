package com.digitalevidence.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.digitalevidence.exception.ValidationException;
import com.digitalevidence.model.CaseFile;
import com.digitalevidence.model.CustodyEvent;
import com.digitalevidence.model.DigitalEvidence;
import com.digitalevidence.model.EvidenceType;
import com.digitalevidence.repository.EvidenceRepository;

public class ChainOfCustodyService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EvidenceRepository repository;

    public ChainOfCustodyService(EvidenceRepository repository) {
        this.repository = repository;
    }

    public CaseFile createCase(String caseId, String title, String investigator) {
        if (repository.caseExists(caseId)) {
            throw new ValidationException("Case already exists: " + caseId);
        }
        CaseFile caseFile = new CaseFile(caseId, title, investigator);
        repository.saveCase(caseFile);
        return caseFile;
    }

    public DigitalEvidence addEvidence(String caseId, String evidenceId, String description, EvidenceType type, String collectedBy) {
        CaseFile caseFile = repository.findCaseById(caseId)
                .orElseThrow(() -> new ValidationException("Case not found: " + caseId));
        if (repository.evidenceExists(evidenceId)) {
            throw new ValidationException("Evidence already exists: " + evidenceId);
        }
        DigitalEvidence evidence = new DigitalEvidence(evidenceId, caseId, description, type, collectedBy);
        repository.saveEvidence(evidence);
        caseFile.addEvidenceId(evidence.getEvidenceId());
        repository.saveCase(caseFile);
        return evidence;
    }

    public DigitalEvidence transferCustody(String evidenceId, String newCustodian, String reason) {
        DigitalEvidence evidence = repository.findEvidenceById(evidenceId)
                .orElseThrow(() -> new ValidationException("Evidence not found: " + evidenceId));
        evidence.transferCustody(newCustodian, reason);
        repository.saveEvidence(evidence);
        return evidence;
    }

    public String generateCaseReport(String caseId) {
        CaseFile caseFile = repository.findCaseById(caseId)
                .orElseThrow(() -> new ValidationException("Case not found: " + caseId));
        List<DigitalEvidence> evidenceList = repository.findEvidenceByCaseId(caseId);

        StringBuilder report = new StringBuilder();
        report.append("Case Report\n");
        report.append("===========\n");
        report.append("Case ID: ").append(caseFile.getCaseId()).append('\n');
        report.append("Title: ").append(caseFile.getCaseTitle()).append('\n');
        report.append("Investigator: ").append(caseFile.getInvestigatorName()).append('\n');
        report.append("Created At: ").append(caseFile.getCreatedAt().format(DATE_TIME_FORMATTER)).append('\n');
        report.append("Evidence Count: ").append(evidenceList.size()).append('\n');

        for (DigitalEvidence evidence : evidenceList) {
            report.append('\n');
            report.append("Evidence ID: ").append(evidence.getEvidenceId()).append('\n');
            report.append("Description: ").append(evidence.getDescription()).append('\n');
            report.append("Type: ").append(evidence.getType()).append('\n');
            report.append("Collected By: ").append(evidence.getCollectedBy()).append('\n');
            report.append("Current Custodian: ").append(evidence.getCurrentCustodian()).append('\n');
            report.append("Status: ").append(evidence.getStatus()).append('\n');
            report.append("Custody History:\n");
            for (CustodyEvent event : evidence.getCustodyHistory()) {
                report.append("  - ").append(event.formattedTimestamp())
                        .append(" | ").append(event.fromCustodian())
                        .append(" -> ").append(event.toCustodian())
                        .append(" | ").append(event.reason())
                        .append('\n');
            }
        }
        return report.toString();
    }

    public List<CaseFile> listCases() {
        return repository.findAllCases().stream().collect(Collectors.toList());
    }
}
