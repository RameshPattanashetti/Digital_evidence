package com.digitalevidence.mvc.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalevidence.mvc.dto.AddEvidenceRequest;
import com.digitalevidence.mvc.dto.CreateCaseRequest;
import com.digitalevidence.mvc.dto.TransferCustodyRequest;
import com.digitalevidence.mvc.model.CaseFileEntity;
import com.digitalevidence.mvc.model.CustodyEventEntity;
import com.digitalevidence.mvc.model.EvidenceEntity;
import com.digitalevidence.mvc.model.EvidenceStatus;
import com.digitalevidence.mvc.repository.CaseFileRepository;
import com.digitalevidence.mvc.repository.EvidenceRepository;

@Service
@Profile("!mongo")
public class EvidenceManagementService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CaseFileRepository caseFileRepository;
    private final EvidenceRepository evidenceRepository;

    public EvidenceManagementService(CaseFileRepository caseFileRepository, EvidenceRepository evidenceRepository) {
        this.caseFileRepository = caseFileRepository;
        this.evidenceRepository = evidenceRepository;
    }

    @Transactional
    public void createCase(CreateCaseRequest request) {
        if (caseFileRepository.existsById(request.caseId())) {
            throw new IllegalArgumentException("Case already exists: " + request.caseId());
        }
        CaseFileEntity caseFile = new CaseFileEntity();
        caseFile.setCaseId(request.caseId().trim());
        caseFile.setCaseTitle(request.caseTitle().trim());
        caseFile.setInvestigatorName(request.investigatorName().trim());
        caseFile.setCreatedAt(LocalDateTime.now());
        caseFileRepository.save(caseFile);
    }

    @Transactional
    public void addEvidence(AddEvidenceRequest request) {
        CaseFileEntity caseFile = caseFileRepository.findById(request.caseId())
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + request.caseId()));
        if (evidenceRepository.existsById(request.evidenceId())) {
            throw new IllegalArgumentException("Evidence already exists: " + request.evidenceId());
        }

        EvidenceEntity evidence = new EvidenceEntity();
        evidence.setEvidenceId(request.evidenceId().trim());
        evidence.setCaseFile(caseFile);
        evidence.setDescription(request.description().trim());
        evidence.setType(request.type());
        evidence.setCollectedBy(request.collectedBy().trim());
        evidence.setCollectedAt(LocalDateTime.now());
        evidence.setCurrentCustodian(request.collectedBy().trim());
        evidence.setStatus(EvidenceStatus.IN_CUSTODY);

        CustodyEventEntity initialEvent = new CustodyEventEntity();
        initialEvent.setEvidence(evidence);
        initialEvent.setFromCustodian("SYSTEM");
        initialEvent.setToCustodian(request.collectedBy().trim());
        initialEvent.setReason("Initial collection");
        initialEvent.setEventTime(LocalDateTime.now());
        evidence.getCustodyEvents().add(initialEvent);

        evidenceRepository.save(evidence);
    }

    @Transactional
    public void transferCustody(TransferCustodyRequest request) {
        EvidenceEntity evidence = evidenceRepository.findById(request.evidenceId())
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + request.evidenceId()));

        CustodyEventEntity event = new CustodyEventEntity();
        event.setEvidence(evidence);
        event.setFromCustodian(evidence.getCurrentCustodian());
        event.setToCustodian(request.newCustodian().trim());
        event.setReason(request.reason().trim());
        event.setEventTime(LocalDateTime.now());

        evidence.getCustodyEvents().add(event);
        evidence.setCurrentCustodian(request.newCustodian().trim());
        evidence.setStatus(EvidenceStatus.TRANSFERRED);

        evidenceRepository.save(evidence);
    }

    @Transactional
    public void archiveEvidence(String evidenceId) {
        EvidenceEntity evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceId));
        evidence.setStatus(EvidenceStatus.ARCHIVED);
        evidenceRepository.save(evidence);
    }

    @Transactional(readOnly = true)
    public List<CaseFileEntity> listCases() {
        return caseFileRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<EvidenceEntity> listEvidenceByCaseId(String caseId) {
        return evidenceRepository.findByCaseFileCaseId(caseId);
    }

    @Transactional(readOnly = true)
    public EvidenceEntity findEvidenceById(String evidenceId) {
        return evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceId));
    }

    @Transactional(readOnly = true)
    public String generateCaseReport(String caseId) {
        CaseFileEntity caseFile = caseFileRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));
        List<EvidenceEntity> evidenceList = evidenceRepository.findByCaseFileCaseId(caseId);

        StringBuilder report = new StringBuilder();
        report.append("Case Report\n");
        report.append("===========\n");
        report.append("Case ID: ").append(caseFile.getCaseId()).append('\n');
        report.append("Title: ").append(caseFile.getCaseTitle()).append('\n');
        report.append("Investigator: ").append(caseFile.getInvestigatorName()).append('\n');
        report.append("Created At: ").append(caseFile.getCreatedAt().format(FORMATTER)).append('\n');
        report.append("Evidence Count: ").append(evidenceList.size()).append('\n');

        for (EvidenceEntity evidence : evidenceList) {
            report.append('\n');
            report.append("Evidence ID: ").append(evidence.getEvidenceId()).append('\n');
            report.append("Description: ").append(evidence.getDescription()).append('\n');
            report.append("Type: ").append(evidence.getType()).append('\n');
            report.append("Collected By: ").append(evidence.getCollectedBy()).append('\n');
            report.append("Current Custodian: ").append(evidence.getCurrentCustodian()).append('\n');
            report.append("Status: ").append(evidence.getStatus()).append('\n');
            report.append("Custody History:\n");
            for (CustodyEventEntity event : evidence.getCustodyEvents()) {
                report.append("  - ").append(event.getEventTime().format(FORMATTER))
                        .append(" | ").append(event.getFromCustodian())
                        .append(" -> ").append(event.getToCustodian())
                        .append(" | ").append(event.getReason())
                        .append('\n');
            }
        }
        return report.toString();
    }
}
