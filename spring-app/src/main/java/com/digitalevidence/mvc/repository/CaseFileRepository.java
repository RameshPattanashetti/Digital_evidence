package com.digitalevidence.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalevidence.mvc.model.CaseFileEntity;

public interface CaseFileRepository extends JpaRepository<CaseFileEntity, String> {
}
