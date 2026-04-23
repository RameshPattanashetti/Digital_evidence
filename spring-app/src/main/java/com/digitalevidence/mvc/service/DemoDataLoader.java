package com.digitalevidence.mvc.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.digitalevidence.mvc.dto.AddEvidenceRequest;
import com.digitalevidence.mvc.dto.CreateCaseRequest;
import com.digitalevidence.mvc.dto.TransferCustodyRequest;
import com.digitalevidence.mvc.model.EvidenceType;

@Component
@Profile("!mongo")
public class DemoDataLoader implements CommandLineRunner {
    private final EvidenceManagementService service;

    public DemoDataLoader(EvidenceManagementService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        if (!service.listCases().isEmpty()) {
            return;
        }

        service.createCase(new CreateCaseRequest("CASE-001", "Unauthorized Cloud Access", "Inspector Sharma"));
        service.addEvidence(new AddEvidenceRequest("CASE-001", "EV-001", "Laptop image containing login traces", EvidenceType.IMAGE, "Inspector Sharma"));
        service.addEvidence(new AddEvidenceRequest("CASE-001", "EV-002", "Server log export from affected account", EvidenceType.DOCUMENT, "Inspector Sharma"));
        service.transferCustody(new TransferCustodyRequest("EV-002", "Forensic Lab", "Digital forensic analysis"));
    }
}
