package io.github.kawakaze430.finance;

import java.util.UUID;
import java.math.BigDecimal;
public record AccountView(UUID id, AccountCode code, String currency, BigDecimal balance) {}
