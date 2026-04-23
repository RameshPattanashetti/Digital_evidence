package com.digitalevidence.mvc.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalevidence.mvc.backend.dto.custody.CustodyRecordRequest;
import com.digitalevidence.mvc.backend.dto.custody.CustodyRecordResponse;
import com.digitalevidence.mvc.backend.entity.CustodyRecord;
import com.digitalevidence.mvc.backend.entity.EvidenceRecord;
import com.digitalevidence.mvc.backend.repository.CustodyRecordRepository;

@Service
@Profile("!mongo")
public class CustodyService {

    private static final Logger log = LoggerFactory.getLogger(CustodyService.class);

    private final CustodyRecordRepository custodyRecordRepository;
    private final EvidenceService evidenceService;

    public CustodyService(CustodyRecordRepository custodyRecordRepository, EvidenceService evidenceService) {
        this.custodyRecordRepository = custodyRecordRepository;
        this.evidenceService = evidenceService;
    }

    @Transactional
    public CustodyRecordResponse addRecord(CustodyRecordRequest request) {
        EvidenceRecord evidence = evidenceService.getEvidenceEntityById(request.evidenceId());

        CustodyRecord record = new CustodyRecord();
        record.setEvidence(evidence);
        record.setFromPerson(request.fromPerson().trim());
        record.setToPerson(request.toPerson().trim());
        record.setTimestamp(request.timestamp() == null ? LocalDateTime.now() : request.timestamp());
        record.setRemarks(request.remarks().trim());

        CustodyRecord saved = custodyRecordRepository.save(record);
        log.info("Added custody record id={} for evidence id={}", saved.getId(), request.evidenceId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CustodyRecordResponse> getHistoryByEvidenceId(Long evidenceId) {
        evidenceService.getEvidenceEntityById(evidenceId);
        return custodyRecordRepository.findByEvidenceIdOrderByTimestampAsc(evidenceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CustodyRecordResponse toResponse(CustodyRecord record) {
        return new CustodyRecordResponse(
                record.getId(),
                record.getEvidence().getId(),
                record.getFromPerson(),
                record.getToPerson(),
                record.getTimestamp(),
                record.getRemarks());
    }
}
