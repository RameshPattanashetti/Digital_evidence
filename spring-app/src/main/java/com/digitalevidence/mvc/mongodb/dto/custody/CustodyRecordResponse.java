package com.digitalevidence.mvc.mongodb.dto.custody;

import java.time.LocalDateTime;

public record CustodyRecordResponse(
        String id,
        String evidenceId,
        String fromPerson,
        String toPerson,
        LocalDateTime timestamp,
        String remarks) {
}
