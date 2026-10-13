package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;
public class MoneyValidationTest {
 @Test void preservesPaiseExactly(){assertThat(new Money(new BigDecimal("0.10"),"INR").amount().add(new Money(new BigDecimal("0.20"),"INR").amount())).isEqualByComparingTo("0.30");}
 @Test void rejectsSubPaiseAmounts(){assertThatThrownBy(()->new Money(new BigDecimal("1.001"),"INR")).isInstanceOf(DomainException.class);}
 @Test void rejectsNegativeAmounts(){assertThatThrownBy(()->new Money(new BigDecimal("-1"),"INR")).isInstanceOf(DomainException.class);}
 @Test void rejectsZeroInvoiceAmount(){assertThatThrownBy(()->Money.positive(BigDecimal.ZERO,"INR")).isInstanceOf(DomainException.class);}
 @Test void rejectsOversizeAmounts(){assertThatThrownBy(()->new Money(new BigDecimal("100000000000000.00"),"INR")).isInstanceOf(DomainException.class);}
}
