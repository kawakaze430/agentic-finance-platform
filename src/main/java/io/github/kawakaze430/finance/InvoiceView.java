package io.github.kawakaze430.finance;

import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;
public record InvoiceView(UUID id, UUID vendorId, String vendorName, String reference, BigDecimal amount, String currency, LocalDate dueDate, InvoiceStatus status, String createdBy, long version) {}
