package com.digitalevidence.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.digitalevidence.exception.ValidationException;

public record CustodyEvent(String fromCustodian, String toCustodian, String reason, LocalDateTime timestamp) implements Serializable {
    private static final long serialVersionUID = 1L;

    public CustodyEvent {
        if (isBlank(fromCustodian)) {
            throw new ValidationException("From custodian is required");
        }
        if (isBlank(toCustodian)) {
            throw new ValidationException("To custodian is required");
        }
        if (isBlank(reason)) {
            throw new ValidationException("Reason is required");
        }
        if (timestamp == null) {
            throw new ValidationException("Timestamp is required");
        }
    }

    public static CustodyEvent create(String fromCustodian, String toCustodian, String reason) {
        return new CustodyEvent(fromCustodian, toCustodian, reason, LocalDateTime.now());
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public String formattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
