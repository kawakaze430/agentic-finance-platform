package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;import java.util.List;
public class JournalBalanceTest extends IntegrationSupport {
 @Test void unbalancedJournalIsRejectedBeforeWrites(){long n=journalRepository.count();var r=new Requests.Journal(key(),"INR","Unbalanced",List.of(new Requests.Line(AccountCode.CASH,BigDecimal.TEN,BigDecimal.ZERO),new Requests.Line(AccountCode.EQUITY,BigDecimal.ZERO,BigDecimal.ONE)));assertThatThrownBy(()->ledger.post(r,"admin")).isInstanceOf(DomainException.class);assertThat(journalRepository.count()).isEqualTo(n);}
 @Test void cannotDebitAndCreditSameLine(){var r=new Requests.Journal(key(),"INR","Invalid",List.of(new Requests.Line(AccountCode.CASH,BigDecimal.TEN,BigDecimal.TEN),new Requests.Line(AccountCode.EQUITY,BigDecimal.TEN,BigDecimal.TEN)));assertThatThrownBy(()->ledger.post(r,"admin")).isInstanceOf(DomainException.class);}
}
