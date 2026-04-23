package com.digitalevidence.mvc.mongodb.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.digitalevidence.mvc.mongodb.model.CaseFile;
import com.digitalevidence.mvc.mongodb.model.CaseStatus;
import com.digitalevidence.mvc.mongodb.model.CustodyRecord;
import com.digitalevidence.mvc.mongodb.model.Evidence;
import com.digitalevidence.mvc.mongodb.model.User;
import com.digitalevidence.mvc.mongodb.model.UserRole;
import com.digitalevidence.mvc.mongodb.repository.CaseRepository;
import com.digitalevidence.mvc.mongodb.repository.CustodyRecordRepository;
import com.digitalevidence.mvc.mongodb.repository.EvidenceRepository;
import com.digitalevidence.mvc.mongodb.repository.UserRepository;

@Component
@Profile("mongo")
public class MongoSampleDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoSampleDataInitializer.class);

    private final UserRepository userRepository;
    private final CaseRepository caseRepository;
    private final EvidenceRepository evidenceRepository;
    private final CustodyRecordRepository custodyRecordRepository;

    public MongoSampleDataInitializer(
            UserRepository userRepository,
            CaseRepository caseRepository,
            EvidenceRepository evidenceRepository,
            CustodyRecordRepository custodyRecordRepository) {
        this.userRepository = userRepository;
        this.caseRepository = caseRepository;
        this.evidenceRepository = evidenceRepository;
        this.custodyRecordRepository = custodyRecordRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0 || caseRepository.count() > 0) {
            return;
        }

        User user = new User();
        user.setName("Mongo Admin");
        user.setEmail("mongo.admin@de.local");
        user.setPassword("admin123");
        user.setRole(UserRole.ADMIN);
        user = userRepository.save(user);

        CaseFile caseFile = new CaseFile();
        caseFile.setCaseNumber("CASE-0001");
        caseFile.setTitle("Cloud Account Breach Investigation");
        caseFile.setDescription("Track breach artifacts and ownership handovers.");
        caseFile.setStatus(CaseStatus.OPEN);
        caseFile.setCreatedDate(LocalDateTime.now());
        caseFile = caseRepository.save(caseFile);

        Evidence evidence = new Evidence();
        evidence.setType("LOG");
        evidence.setDescription("Authentication logs for suspicious access window");
        evidence.setDate(LocalDateTime.now());
        evidence.setLocation("Digital Vault - Rack 2");
        evidence.setCaseId(caseFile.getId());
        evidence = evidenceRepository.save(evidence);

        CustodyRecord custodyRecord = new CustodyRecord();
        custodyRecord.setEvidenceId(evidence.getId());
        custodyRecord.setFromPerson("Collection Desk");
        custodyRecord.setToPerson("Investigator Nisha");
        custodyRecord.setTimestamp(LocalDateTime.now());
        custodyRecord.setRemarks("Initial evidence transfer for forensic review");
        custodyRecordRepository.save(custodyRecord);

        log.info("Mongo sample data initialized with userId={} caseId={} evidenceId={}", user.getId(), caseFile.getId(), evidence.getId());
    }
}
