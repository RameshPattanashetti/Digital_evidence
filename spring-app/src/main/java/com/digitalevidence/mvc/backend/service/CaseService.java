package com.digitalevidence.mvc.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalevidence.mvc.backend.dto.caseapi.CaseRequest;
import com.digitalevidence.mvc.backend.dto.caseapi.CaseResponse;
import com.digitalevidence.mvc.backend.entity.CaseRecord;
import com.digitalevidence.mvc.backend.exception.ResourceNotFoundException;
import com.digitalevidence.mvc.backend.repository.CaseRecordRepository;

@Service
@Profile("!mongo")
public class CaseService {

    private static final Logger log = LoggerFactory.getLogger(CaseService.class);

    private final CaseRecordRepository caseRecordRepository;

    public CaseService(CaseRecordRepository caseRecordRepository) {
        this.caseRecordRepository = caseRecordRepository;
    }

    @Transactional
    public CaseResponse createCase(CaseRequest request) {
        CaseRecord caseRecord = new CaseRecord();
        caseRecord.setTitle(request.title().trim());
        caseRecord.setDescription(request.description().trim());
        caseRecord.setStatus(request.status());
        caseRecord.setCreatedDate(LocalDateTime.now());

        CaseRecord saved = caseRecordRepository.save(caseRecord);
        log.info("Created case with id={}", saved.getId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CaseResponse> getAllCases() {
        return caseRecordRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CaseResponse getCaseById(Long id) {
        CaseRecord caseRecord = caseRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));
        return toResponse(caseRecord);
    }

    @Transactional
    public CaseResponse updateCase(Long id, CaseRequest request) {
        CaseRecord existing = caseRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));

        existing.setTitle(request.title().trim());
        existing.setDescription(request.description().trim());
        existing.setStatus(request.status());

        CaseRecord updated = caseRecordRepository.save(existing);
        log.info("Updated case with id={}", id);
        return toResponse(updated);
    }

    @Transactional
    public void deleteCase(Long id) {
        if (!caseRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Case not found with id: " + id);
        }
        caseRecordRepository.deleteById(id);
        log.info("Deleted case with id={}", id);
    }

    private CaseResponse toResponse(CaseRecord caseRecord) {
        return new CaseResponse(
                caseRecord.getId(),
                caseRecord.getTitle(),
                caseRecord.getDescription(),
                caseRecord.getStatus(),
                caseRecord.getCreatedDate());
    }
}
