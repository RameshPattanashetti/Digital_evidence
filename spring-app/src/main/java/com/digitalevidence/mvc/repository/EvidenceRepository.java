package com.digitalevidence.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalevidence.mvc.model.EvidenceEntity;

public interface EvidenceRepository extends JpaRepository<EvidenceEntity, String> {
    List<EvidenceEntity> findByCaseFileCaseId(String caseId);
}
