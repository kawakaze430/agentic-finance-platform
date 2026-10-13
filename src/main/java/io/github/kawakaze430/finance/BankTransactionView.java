package io.github.kawakaze430.finance;

import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;
public record BankTransactionView(UUID id, String externalId, String reference, String counterparty, BigDecimal amount, String currency, LocalDate bookedOn, boolean matched) {}
