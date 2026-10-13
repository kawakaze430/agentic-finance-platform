package io.github.kawakaze430.finance;

import java.util.UUID;
import java.time.Instant;
import java.util.List;
public record JournalView(UUID id, String reference, String currency, String description, Instant createdAt, List<LedgerLineView> lines) {}
