package com.digitalevidence.mvc.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalevidence.mvc.backend.dto.evidence.EvidenceRequest;
import com.digitalevidence.mvc.backend.dto.evidence.EvidenceResponse;
import com.digitalevidence.mvc.backend.entity.CaseRecord;
import com.digitalevidence.mvc.backend.entity.EvidenceRecord;
import com.digitalevidence.mvc.backend.exception.ResourceNotFoundException;
import com.digitalevidence.mvc.backend.repository.CaseRecordRepository;
import com.digitalevidence.mvc.backend.repository.EvidenceRecordRepository;

@Service
@Profile("!mongo")
public class EvidenceService {

    private static final Logger log = LoggerFactory.getLogger(EvidenceService.class);

    private final EvidenceRecordRepository evidenceRecordRepository;
    private final CaseRecordRepository caseRecordRepository;

    public EvidenceService(EvidenceRecordRepository evidenceRecordRepository, CaseRecordRepository caseRecordRepository) {
        this.evidenceRecordRepository = evidenceRecordRepository;
        this.caseRecordRepository = caseRecordRepository;
    }

    @Transactional
    public EvidenceResponse addEvidence(EvidenceRequest request) {
        CaseRecord caseRecord = caseRecordRepository.findById(request.caseId())
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + request.caseId()));

        EvidenceRecord evidence = new EvidenceRecord();
        evidence.setType(request.type().trim());
        evidence.setDescription(request.description().trim());
        evidence.setDate(request.date());
        evidence.setLocation(request.location().trim());
        evidence.setCaseRecord(caseRecord);

        EvidenceRecord saved = evidenceRecordRepository.save(evidence);
        log.info("Added evidence id={} to case id={}", saved.getId(), request.caseId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EvidenceResponse> getAllEvidence() {
        return evidenceRecordRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EvidenceResponse getEvidenceById(Long id) {
        EvidenceRecord evidence = evidenceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence not found with id: " + id));
        return toResponse(evidence);
    }

    @Transactional
    public void deleteEvidence(Long id) {
        if (!evidenceRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evidence not found with id: " + id);
        }
        evidenceRecordRepository.deleteById(id);
        log.info("Deleted evidence with id={}", id);
    }

    @Transactional(readOnly = true)
    public EvidenceRecord getEvidenceEntityById(Long id) {
        return evidenceRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence not found with id: " + id));
    }

    private EvidenceResponse toResponse(EvidenceRecord evidence) {
        return new EvidenceResponse(
                evidence.getId(),
                evidence.getType(),
                evidence.getDescription(),
                evidence.getDate(),
                evidence.getLocation(),
                evidence.getCaseRecord().getId());
    }
}
