package com.digitalevidence.mvc.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalevidence.mvc.backend.entity.CustodyRecord;

public interface CustodyRecordRepository extends JpaRepository<CustodyRecord, Long> {
    List<CustodyRecord> findByEvidenceIdOrderByTimestampAsc(Long evidenceId);
}
