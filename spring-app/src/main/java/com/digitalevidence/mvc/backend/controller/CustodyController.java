package com.digitalevidence.mvc.backend.controller;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.digitalevidence.mvc.backend.dto.custody.CustodyRecordRequest;
import com.digitalevidence.mvc.backend.dto.custody.CustodyRecordResponse;
import com.digitalevidence.mvc.backend.service.CustodyService;

import jakarta.validation.Valid;

@RestController
@Profile("!mongo")
@RequestMapping("/api/v1/custody")
public class CustodyController {

    private final CustodyService custodyService;

    public CustodyController(CustodyService custodyService) {
        this.custodyService = custodyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustodyRecordResponse addCustodyRecord(@Valid @RequestBody CustodyRecordRequest request) {
        return custodyService.addRecord(request);
    }

    @GetMapping("/evidence/{evidenceId}")
    public List<CustodyRecordResponse> getCustodyHistoryByEvidenceId(@PathVariable Long evidenceId) {
        return custodyService.getHistoryByEvidenceId(evidenceId);
    }
}
