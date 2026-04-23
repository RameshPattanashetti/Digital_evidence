package com.digitalevidence.mvc.backend.controller;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.digitalevidence.mvc.backend.dto.caseapi.CaseRequest;
import com.digitalevidence.mvc.backend.dto.caseapi.CaseResponse;
import com.digitalevidence.mvc.backend.dto.common.ApiMessageResponse;
import com.digitalevidence.mvc.backend.service.CaseService;

import jakarta.validation.Valid;

@RestController
@Profile("!mongo")
@RequestMapping("/api/v1/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CaseResponse createCase(@Valid @RequestBody CaseRequest request) {
        return caseService.createCase(request);
    }

    @GetMapping
    public List<CaseResponse> getAllCases() {
        return caseService.getAllCases();
    }

    @GetMapping("/{id}")
    public CaseResponse getCaseById(@PathVariable Long id) {
        return caseService.getCaseById(id);
    }

    @PutMapping("/{id}")
    public CaseResponse updateCase(@PathVariable Long id, @Valid @RequestBody CaseRequest request) {
        return caseService.updateCase(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiMessageResponse deleteCase(@PathVariable Long id) {
        caseService.deleteCase(id);
        return new ApiMessageResponse("Case deleted successfully");
    }
}
