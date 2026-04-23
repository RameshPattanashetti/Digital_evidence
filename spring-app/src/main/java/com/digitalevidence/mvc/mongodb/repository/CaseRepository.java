package com.digitalevidence.mvc.mongodb.repository;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.digitalevidence.mvc.mongodb.model.CaseFile;

@Profile("mongo")
public interface CaseRepository extends MongoRepository<CaseFile, String> {
	Optional<CaseFile> findFirstByCaseNumberIsNotNullOrderByCaseNumberDesc();

	Optional<CaseFile> findByCaseNumber(String caseNumber);
}
