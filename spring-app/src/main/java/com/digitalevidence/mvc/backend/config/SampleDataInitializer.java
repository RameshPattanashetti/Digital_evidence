package com.digitalevidence.mvc.backend.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.digitalevidence.mvc.backend.entity.CaseRecord;
import com.digitalevidence.mvc.backend.entity.CaseStatus;
import com.digitalevidence.mvc.backend.entity.CustodyRecord;
import com.digitalevidence.mvc.backend.entity.EvidenceRecord;
import com.digitalevidence.mvc.backend.entity.User;
import com.digitalevidence.mvc.backend.entity.UserRole;
import com.digitalevidence.mvc.backend.repository.CaseRecordRepository;
import com.digitalevidence.mvc.backend.repository.CustodyRecordRepository;
import com.digitalevidence.mvc.backend.repository.EvidenceRecordRepository;
import com.digitalevidence.mvc.backend.repository.UserRepository;

@Component
@Profile("!mongo")
public class SampleDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleDataInitializer.class);

    private final UserRepository userRepository;
    private final CaseRecordRepository caseRecordRepository;
    private final EvidenceRecordRepository evidenceRecordRepository;
    private final CustodyRecordRepository custodyRecordRepository;

    public SampleDataInitializer(
            UserRepository userRepository,
            CaseRecordRepository caseRecordRepository,
            EvidenceRecordRepository evidenceRecordRepository,
            CustodyRecordRepository custodyRecordRepository) {
        this.userRepository = userRepository;
        this.caseRecordRepository = caseRecordRepository;
        this.evidenceRecordRepository = evidenceRecordRepository;
        this.custodyRecordRepository = custodyRecordRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0 || caseRecordRepository.count() > 0) {
            return;
        }

        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@de.local");
        admin.setPassword("admin123");
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);

        CaseRecord caseRecord = new CaseRecord();
        caseRecord.setTitle("Corporate Data Leak Investigation");
        caseRecord.setDescription("Investigate unauthorized export of sensitive records.");
        caseRecord.setStatus(CaseStatus.OPEN);
        caseRecord.setCreatedDate(LocalDateTime.now());
        caseRecord = caseRecordRepository.save(caseRecord);

        EvidenceRecord evidence = new EvidenceRecord();
        evidence.setType("LOG_FILE");
        evidence.setDescription("Firewall logs for suspicious traffic window.");
        evidence.setDate(LocalDateTime.now());
        evidence.setLocation("Forensic Vault A");
        evidence.setCaseRecord(caseRecord);
        evidence = evidenceRecordRepository.save(evidence);

        CustodyRecord custody = new CustodyRecord();
        custody.setEvidence(evidence);
        custody.setFromPerson("Collection Desk");
        custody.setToPerson("Investigator Ravi");
        custody.setTimestamp(LocalDateTime.now());
        custody.setRemarks("Initial handover for analysis");
        custodyRecordRepository.save(custody);

        log.info("Sample backend data initialized for User/Case/Evidence/Custody modules");
    }
}
