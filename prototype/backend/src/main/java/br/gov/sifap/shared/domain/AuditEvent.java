package br.gov.sifap.shared.domain;

import java.time.Instant;

/**
 * Immutable audit event record published by all bounded contexts.
 * Actions: IN (insert), AL (alter), CO (consult), CN (cancel), DV (return), EX (exclude).
 */
public record AuditEvent(
        String action,
        String entityType,
        String entityId,
        String userId,
        String description,
        Instant timestamp
) {
    public AuditEvent {
        if (action == null || !action.matches("IN|AL|CO|CN|DV|EX")) {
            throw new IllegalArgumentException("Invalid audit action: " + action);
        }
    }
}
