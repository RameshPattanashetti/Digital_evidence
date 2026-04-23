package com.digitalevidence.mvc.backend.controller;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.digitalevidence.mvc.backend.dto.common.ApiMessageResponse;
import com.digitalevidence.mvc.backend.dto.evidence.EvidenceRequest;
import com.digitalevidence.mvc.backend.dto.evidence.EvidenceResponse;
import com.digitalevidence.mvc.backend.service.EvidenceService;

import jakarta.validation.Valid;

@RestController
@Profile("!mongo")
@RequestMapping("/api/v1/evidence")
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenceResponse addEvidence(@Valid @RequestBody EvidenceRequest request) {
        return evidenceService.addEvidence(request);
    }

    @GetMapping
    public List<EvidenceResponse> getAllEvidence() {
        return evidenceService.getAllEvidence();
    }

    @GetMapping("/{id}")
    public EvidenceResponse getEvidenceById(@PathVariable Long id) {
        return evidenceService.getEvidenceById(id);
    }

    @DeleteMapping("/{id}")
    public ApiMessageResponse deleteEvidence(@PathVariable Long id) {
        evidenceService.deleteEvidence(id);
        return new ApiMessageResponse("Evidence deleted successfully");
    }
}
