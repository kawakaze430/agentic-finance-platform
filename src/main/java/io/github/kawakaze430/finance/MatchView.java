package io.github.kawakaze430.finance;

import java.util.UUID;
import java.time.Instant;
public record MatchView(UUID id, UUID invoiceId, UUID bankTransactionId, String matchedBy, Instant matchedAt) {}
