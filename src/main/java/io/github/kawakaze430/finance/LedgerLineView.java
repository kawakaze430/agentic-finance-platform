package io.github.kawakaze430.finance;

import java.math.BigDecimal;
public record LedgerLineView(AccountCode account, BigDecimal debit, BigDecimal credit) {}
