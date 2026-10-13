package io.github.kawakaze430.finance;

import java.math.BigDecimal;
public record DashboardView(String currency, long invoices, long awaitingApproval, long paid, BigDecimal outstandingAmount, long unmatchedTransactions, String agentMode) {}
