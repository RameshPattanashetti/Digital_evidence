package com.digitalevidence.bootstrap;

import com.digitalevidence.model.EvidenceType;
import com.digitalevidence.service.ChainOfCustodyService;

public final class DemoDataSeeder {
    private DemoDataSeeder() {
    }

    public static void seed(ChainOfCustodyService service) {
        service.createCase("CASE-001", "Unauthorized Cloud Access", "Inspector Sharma");
        service.addEvidence("CASE-001", "EV-001", "Laptop image containing login traces", EvidenceType.IMAGE, "Inspector Sharma");
        service.addEvidence("CASE-001", "EV-002", "Server log export from the affected account", EvidenceType.DOCUMENT, "Inspector Sharma");
        service.transferCustody("EV-002", "Forensic Lab", "Digital forensic analysis");
    }
}
