package com.server.auth.domain;

import java.time.LocalDateTime;

public record LoginAttempDomain (
        String email,
        boolean success,
        LocalDateTime createdAt
) {
}
