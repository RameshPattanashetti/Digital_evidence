package com.digitalevidence.mvc.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalevidence.mvc.backend.entity.EvidenceRecord;

public interface EvidenceRecordRepository extends JpaRepository<EvidenceRecord, Long> {
    List<EvidenceRecord> findByCaseRecordId(Long caseId);
}
