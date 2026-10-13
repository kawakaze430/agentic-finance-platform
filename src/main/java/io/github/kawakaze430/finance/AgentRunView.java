package io.github.kawakaze430.finance;

import java.util.UUID;
import java.time.Instant;
import java.util.List;
public record AgentRunView(UUID id, UUID invoiceId, long invoiceVersion, String mode, AgentStatus status, Instant startedAt, Recommendation recommendation, List<ToolStep> trace, String errorCode) {}
