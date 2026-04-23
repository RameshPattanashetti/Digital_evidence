package com.digitalevidence.mvc.mongodb.repository;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.digitalevidence.mvc.mongodb.model.CustodyRecord;

@Profile("mongo")
public interface CustodyRecordRepository extends MongoRepository<CustodyRecord, String> {
    List<CustodyRecord> findByEvidenceIdOrderByTimestampAsc(String evidenceId);
}
