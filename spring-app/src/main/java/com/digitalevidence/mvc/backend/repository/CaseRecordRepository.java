package com.digitalevidence.mvc.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalevidence.mvc.backend.entity.CaseRecord;

public interface CaseRecordRepository extends JpaRepository<CaseRecord, Long> {
}
