package com.digitalevidence.mvc.mongodb.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.digitalevidence.mvc.mongodb.dto.caseapi.CaseRequest;
import com.digitalevidence.mvc.mongodb.dto.caseapi.CaseResponse;
import com.digitalevidence.mvc.mongodb.exception.ResourceNotFoundException;
import com.digitalevidence.mvc.mongodb.model.CaseFile;
import com.digitalevidence.mvc.mongodb.repository.CaseRepository;

@Service
@Profile("mongo")
public class CaseService {

    private static final Logger log = LoggerFactory.getLogger(CaseService.class);
    private static final Pattern CASE_NUMBER_PATTERN = Pattern.compile("CASE-(\\d{4})");

    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public CaseResponse createCase(CaseRequest request) {
        CaseFile caseFile = new CaseFile();
        caseFile.setCaseNumber(nextCaseNumber());
        caseFile.setTitle(request.title().trim());
        caseFile.setDescription(request.description().trim());
        caseFile.setStatus(request.status());
        caseFile.setCreatedDate(LocalDateTime.now());

        CaseFile saved = caseRepository.save(caseFile);
        log.info("Mongo case created with id={}", saved.getId());
        return toResponse(saved);
    }

    public List<CaseResponse> getAllCases() {
        return caseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CaseResponse getCaseById(String id) {
        CaseFile caseFile = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));
        return toResponse(caseFile);
    }

    public CaseResponse updateCase(String id, CaseRequest request) {
        CaseFile existing = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));

        existing.setTitle(request.title().trim());
        existing.setDescription(request.description().trim());
        existing.setStatus(request.status());

        CaseFile updated = caseRepository.save(existing);
        log.info("Mongo case updated with id={}", id);
        return toResponse(updated);
    }

    public void deleteCase(String id) {
        if (!caseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Case not found with id: " + id);
        }
        caseRepository.deleteById(id);
        log.info("Mongo case deleted with id={}", id);
    }

    public CaseFile getCaseEntityById(String id) {
        return caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + id));
    }

    public CaseFile getCaseEntityByIdOrCaseNumber(String reference) {
        String normalized = reference == null ? "" : reference.trim();
        return caseRepository.findById(normalized)
                .or(() -> caseRepository.findByCaseNumber(normalized))
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id or case number: " + reference));
    }

    private CaseResponse toResponse(CaseFile caseFile) {
        return new CaseResponse(
                caseFile.getId(),
                caseFile.getCaseNumber(),
                caseFile.getTitle(),
                caseFile.getDescription(),
                caseFile.getStatus(),
                caseFile.getCreatedDate());
    }

    private String nextCaseNumber() {
        return caseRepository.findFirstByCaseNumberIsNotNullOrderByCaseNumberDesc()
                .map(CaseFile::getCaseNumber)
                .map(this::incrementCaseNumber)
                .orElseGet(() -> String.format("CASE-%04d", caseRepository.findAll().size() + 1));
    }

    private String incrementCaseNumber(String current) {
        Matcher matcher = CASE_NUMBER_PATTERN.matcher(current);
        if (!matcher.matches()) {
            return "CASE-0001";
        }

        int value = Integer.parseInt(matcher.group(1));
        return String.format("CASE-%04d", value + 1);
    }
}
