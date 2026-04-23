package com.digitalevidence.mvc.mongodb.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.digitalevidence.mvc.mongodb.dto.custody.CustodyRecordRequest;
import com.digitalevidence.mvc.mongodb.dto.custody.CustodyRecordResponse;
import com.digitalevidence.mvc.mongodb.model.CustodyRecord;
import com.digitalevidence.mvc.mongodb.repository.CustodyRecordRepository;

@Service
@Profile("mongo")
public class CustodyRecordService {

    private static final Logger log = LoggerFactory.getLogger(CustodyRecordService.class);

    private final CustodyRecordRepository custodyRecordRepository;
    private final EvidenceService evidenceService;

    public CustodyRecordService(CustodyRecordRepository custodyRecordRepository, EvidenceService evidenceService) {
        this.custodyRecordRepository = custodyRecordRepository;
        this.evidenceService = evidenceService;
    }

    public CustodyRecordResponse addRecord(CustodyRecordRequest request) {
        // Validate that referenced evidence exists.
        evidenceService.getEvidenceEntityById(request.evidenceId());

        CustodyRecord record = new CustodyRecord();
        record.setEvidenceId(request.evidenceId().trim());
        record.setFromPerson(request.fromPerson().trim());
        record.setToPerson(request.toPerson().trim());
        record.setTimestamp(request.timestamp() == null ? LocalDateTime.now() : request.timestamp());
        record.setRemarks(request.remarks().trim());

        CustodyRecord saved = custodyRecordRepository.save(record);
        log.info("Mongo custody record added with id={} for evidenceId={}", saved.getId(), saved.getEvidenceId());
        return toResponse(saved);
    }

    public List<CustodyRecordResponse> getHistoryByEvidenceId(String evidenceId) {
        evidenceService.getEvidenceEntityById(evidenceId);
        return custodyRecordRepository.findByEvidenceIdOrderByTimestampAsc(evidenceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CustodyRecordResponse toResponse(CustodyRecord record) {
        return new CustodyRecordResponse(
                record.getId(),
                record.getEvidenceId(),
                record.getFromPerson(),
                record.getToPerson(),
                record.getTimestamp(),
                record.getRemarks());
    }
}
