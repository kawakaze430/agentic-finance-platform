package io.github.kawakaze430.finance;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;
public record Money(BigDecimal amount, String currency) {
    public Money {
        currency = normalizeCurrency(currency);
        if (amount == null || amount.signum() < 0 || amount.scale() > 2 || amount.precision() - amount.scale() > 14)
            throw DomainException.invalid("Amount must be nonnegative with at most two decimal places and 14 integer digits");
        amount = amount.setScale(2);
    }
    public static String normalizeCurrency(String currency) {
        if (currency == null) throw DomainException.invalid("Currency is required");
        String value = currency.trim().toUpperCase(Locale.ROOT);
        if (!Set.of("INR", "USD", "EUR", "GBP").contains(value)) throw DomainException.invalid("Unsupported currency");
        return value;
    }
    public static Money positive(BigDecimal amount, String currency) {
        Money m = new Money(amount, currency);
        if (m.amount.signum() == 0) throw DomainException.invalid("Amount must be greater than zero");
        return m;
    }
}
