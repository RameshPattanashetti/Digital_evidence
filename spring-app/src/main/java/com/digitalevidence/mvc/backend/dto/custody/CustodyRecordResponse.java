package com.digitalevidence.mvc.backend.dto.custody;

import java.time.LocalDateTime;

public record CustodyRecordResponse(
        Long id,
        Long evidenceId,
        String fromPerson,
        String toPerson,
        LocalDateTime timestamp,
        String remarks) {
}
