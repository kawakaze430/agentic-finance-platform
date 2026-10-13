package io.github.kawakaze430.finance;

import java.util.UUID;
import java.time.Instant;
public record AuditView(UUID id, String action, String actor, UUID resourceId, Instant occurredAt) {}
