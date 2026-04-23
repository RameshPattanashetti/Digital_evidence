package com.digitalevidence.mvc.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "custody_events")
public class CustodyEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    private EvidenceEntity evidence;

    @Column(nullable = false)
    private String fromCustodian;

    @Column(nullable = false)
    private String toCustodian;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime eventTime;

    public Long getId() {
        return id;
    }

    public EvidenceEntity getEvidence() {
        return evidence;
    }

    public void setEvidence(EvidenceEntity evidence) {
        this.evidence = evidence;
    }

    public String getFromCustodian() {
        return fromCustodian;
    }

    public void setFromCustodian(String fromCustodian) {
        this.fromCustodian = fromCustodian;
    }

    public String getToCustodian() {
        return toCustodian;
    }

    public void setToCustodian(String toCustodian) {
        this.toCustodian = toCustodian;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
}
