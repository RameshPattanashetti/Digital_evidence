package com.digitalevidence.mvc.mongodb.repository;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.digitalevidence.mvc.mongodb.model.Evidence;

@Profile("mongo")
public interface EvidenceRepository extends MongoRepository<Evidence, String> {
    List<Evidence> findByCaseId(String caseId);
}
