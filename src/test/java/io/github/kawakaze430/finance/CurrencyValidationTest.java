package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class CurrencyValidationTest {
 @Test void normalizesSupportedCurrencies(){assertThat(Money.normalizeCurrency(" inr ")).isEqualTo("INR");}
 @Test void rejectsUnknownCurrency(){assertThatThrownBy(()->Money.normalizeCurrency("XYZ")).isInstanceOf(DomainException.class);}
 @Test void rejectsMissingCurrency(){assertThatThrownBy(()->Money.normalizeCurrency(null)).isInstanceOf(DomainException.class);}
}
