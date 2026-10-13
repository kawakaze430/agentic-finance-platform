package io.github.kawakaze430.finance;

import java.util.List;
public record ReconciliationCandidate(BankTransactionView transaction, int score, List<String> reasons) {}
