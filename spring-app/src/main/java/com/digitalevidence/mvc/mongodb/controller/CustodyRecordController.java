package com.digitalevidence.mvc.mongodb.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.digitalevidence.mvc.mongodb.dto.custody.CustodyRecordRequest;
import com.digitalevidence.mvc.mongodb.dto.custody.CustodyRecordResponse;
import com.digitalevidence.mvc.mongodb.service.CustodyRecordService;

import jakarta.validation.Valid;

@RestController
@Profile("mongo")
@RequestMapping("/api/mongo/v1/custody")
public class CustodyRecordController {

    private final CustodyRecordService custodyRecordService;

    public CustodyRecordController(CustodyRecordService custodyRecordService) {
        this.custodyRecordService = custodyRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustodyRecordResponse addCustodyRecord(@Valid @RequestBody CustodyRecordRequest request) {
        return custodyRecordService.addRecord(request);
    }

    @GetMapping("/evidence/{evidenceId}")
    public List<CustodyRecordResponse> getCustodyHistoryByEvidenceId(@PathVariable String evidenceId) {
        return custodyRecordService.getHistoryByEvidenceId(evidenceId);
    }
}
