package com.digitalevidence.repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.digitalevidence.model.CaseFile;
import com.digitalevidence.model.DigitalEvidence;

public class EvidenceRepository {
    private final Map<String, CaseFile> caseStore = new LinkedHashMap<>();
    private final Map<String, DigitalEvidence> evidenceStore = new LinkedHashMap<>();
    private final Path storageFile;

    public EvidenceRepository() {
        this(Path.of("data", "evidence-store.ser"));
    }

    public EvidenceRepository(Path storageFile) {
        this.storageFile = storageFile;
        load();
    }

    public void saveCase(CaseFile caseFile) {
        caseStore.put(caseFile.getCaseId(), caseFile);
        persist();
    }

    public void saveEvidence(DigitalEvidence evidence) {
        evidenceStore.put(evidence.getEvidenceId(), evidence);
        persist();
    }

    public Optional<CaseFile> findCaseById(String caseId) {
        return Optional.ofNullable(caseStore.get(caseId));
    }

    public Optional<DigitalEvidence> findEvidenceById(String evidenceId) {
        return Optional.ofNullable(evidenceStore.get(evidenceId));
    }

    public Collection<CaseFile> findAllCases() {
        return new ArrayList<>(caseStore.values());
    }

    public List<DigitalEvidence> findEvidenceByCaseId(String caseId) {
        List<DigitalEvidence> evidenceList = new ArrayList<>();
        for (DigitalEvidence evidence : evidenceStore.values()) {
            if (evidence.getCaseId().equals(caseId)) {
                evidenceList.add(evidence);
            }
        }
        return evidenceList;
    }

    public boolean caseExists(String caseId) {
        return caseStore.containsKey(caseId);
    }

    public boolean evidenceExists(String evidenceId) {
        return evidenceStore.containsKey(evidenceId);
    }

    public boolean isEmpty() {
        return caseStore.isEmpty() && evidenceStore.isEmpty();
    }

    private void load() {
        if (!Files.exists(storageFile)) {
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(storageFile))) {
            StoredData storedData = (StoredData) inputStream.readObject();
            caseStore.clear();
            evidenceStore.clear();
            caseStore.putAll(storedData.caseStore());
            evidenceStore.putAll(storedData.evidenceStore());
        } catch (IOException | ClassNotFoundException ex) {
            caseStore.clear();
            evidenceStore.clear();
        }
    }

    private void persist() {
        try {
            Files.createDirectories(storageFile.getParent());
            Path tempFile = storageFile.resolveSibling(storageFile.getFileName() + ".tmp");
            try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(tempFile))) {
                outputStream.writeObject(new StoredData(caseStore, evidenceStore));
            }
            Files.move(tempFile, storageFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to persist repository data", ex);
        }
    }

    private record StoredData(Map<String, CaseFile> caseStore, Map<String, DigitalEvidence> evidenceStore) implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private StoredData {
            caseStore = Collections.unmodifiableMap(new LinkedHashMap<>(caseStore));
            evidenceStore = Collections.unmodifiableMap(new LinkedHashMap<>(evidenceStore));
        }
    }
}
