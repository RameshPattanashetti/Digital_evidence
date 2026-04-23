package com.digitalevidence.mvc.controller;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitalevidence.mvc.dto.AddEvidenceRequest;
import com.digitalevidence.mvc.dto.ApiMessageResponse;
import com.digitalevidence.mvc.dto.CaseSummaryResponse;
import com.digitalevidence.mvc.dto.CreateCaseRequest;
import com.digitalevidence.mvc.dto.EvidenceDetailResponse;
import com.digitalevidence.mvc.dto.TransferCustodyRequest;
import com.digitalevidence.mvc.model.EvidenceEntity;
import com.digitalevidence.mvc.service.EvidenceManagementService;

@RestController
@Profile("!mongo")
@RequestMapping("/api")
public class ApiController {
    private final EvidenceManagementService service;

    public ApiController(EvidenceManagementService service) {
        this.service = service;
    }

    @GetMapping("/cases")
    public List<CaseSummaryResponse> listCases() {
        return service.listCases().stream()
                .map(caseFile -> new CaseSummaryResponse(
                        caseFile.getCaseId(),
                        caseFile.getCaseTitle(),
                        caseFile.getInvestigatorName(),
                service.listEvidenceByCaseId(caseFile.getCaseId()).size()))
                .toList();
    }

    @PostMapping("/cases")
    public ApiMessageResponse createCase(@RequestBody CreateCaseRequest request) {
        service.createCase(request);
        return new ApiMessageResponse("Case created successfully");
    }

    @PostMapping("/evidence")
    public ApiMessageResponse addEvidence(@RequestBody AddEvidenceRequest request) {
        service.addEvidence(request);
        return new ApiMessageResponse("Evidence added successfully");
    }

    @PostMapping("/custody")
    public ApiMessageResponse transferCustody(@RequestBody TransferCustodyRequest request) {
        service.transferCustody(request);
        return new ApiMessageResponse("Custody transferred successfully");
    }

    @PostMapping("/evidence/{evidenceId}/archive")
    public ApiMessageResponse archiveEvidence(@PathVariable String evidenceId) {
        service.archiveEvidence(evidenceId);
        return new ApiMessageResponse("Evidence archived");
    }

    @GetMapping("/evidence/{evidenceId}")
    public EvidenceDetailResponse getEvidence(@PathVariable String evidenceId) {
        EvidenceEntity evidence = service.findEvidenceById(evidenceId);
        return new EvidenceDetailResponse(
                evidence.getEvidenceId(),
                evidence.getCaseFile().getCaseId(),
                evidence.getDescription(),
                evidence.getType(),
                evidence.getCollectedBy(),
                evidence.getCurrentCustodian(),
                evidence.getStatus(),
                evidence.getCollectedAt());
    }

    @GetMapping("/reports/{caseId}")
    public ApiMessageResponse getReport(@PathVariable String caseId) {
        return new ApiMessageResponse(service.generateCaseReport(caseId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiMessageResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiMessageResponse(ex.getMessage()));
    }
}
