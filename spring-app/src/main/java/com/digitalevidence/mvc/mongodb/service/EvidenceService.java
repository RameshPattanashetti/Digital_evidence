package com.digitalevidence.mvc.mongodb.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.digitalevidence.mvc.mongodb.dto.evidence.EvidenceRequest;
import com.digitalevidence.mvc.mongodb.dto.evidence.EvidenceResponse;
import com.digitalevidence.mvc.mongodb.exception.ResourceNotFoundException;
import com.digitalevidence.mvc.mongodb.model.Evidence;
import com.digitalevidence.mvc.mongodb.repository.EvidenceRepository;

@Service
@Profile("mongo")
public class EvidenceService {

    private static final Logger log = LoggerFactory.getLogger(EvidenceService.class);

    private final EvidenceRepository evidenceRepository;
    private final CaseService caseService;

    public EvidenceService(EvidenceRepository evidenceRepository, CaseService caseService) {
        this.evidenceRepository = evidenceRepository;
        this.caseService = caseService;
    }

    public EvidenceResponse addEvidence(EvidenceRequest request) {
        // Validate that referenced case exists.
        var caseFile = caseService.getCaseEntityByIdOrCaseNumber(request.caseId());

        Evidence evidence = new Evidence();
        evidence.setType(request.type().trim());
        evidence.setDescription(request.description().trim());
        evidence.setDate(request.date());
        evidence.setLocation(request.location().trim());
        evidence.setCaseId(caseFile.getId());

        Evidence saved = evidenceRepository.save(evidence);
        log.info("Mongo evidence added with id={} for caseId={}", saved.getId(), saved.getCaseId());
        return toResponse(saved);
    }

    public List<EvidenceResponse> getAllEvidence() {
        return evidenceRepository.findAll().stream().map(this::toResponse).toList();
    }

    public EvidenceResponse getEvidenceById(String id) {
        Evidence evidence = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence not found with id: " + id));
        return toResponse(evidence);
    }

    public void deleteEvidence(String id) {
        if (!evidenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evidence not found with id: " + id);
        }
        evidenceRepository.deleteById(id);
        log.info("Mongo evidence deleted with id={}", id);
    }

    public Evidence getEvidenceEntityById(String id) {
        return evidenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence not found with id: " + id));
    }

    private EvidenceResponse toResponse(Evidence evidence) {
        return new EvidenceResponse(
                evidence.getId(),
                evidence.getType(),
                evidence.getDescription(),
                evidence.getDate(),
                evidence.getLocation(),
                evidence.getCaseId());
    }
}
