package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class ApprovalLedgerTest extends IntegrationSupport {
 @Test void approvalCreatesOneBalancedAccrual(){var i=invoice("123.45");long n=journalRepository.count();var result=invoices.decide(i.id(),decision(),"approver",false,true);assertThat(result.status()).isEqualTo(InvoiceStatus.APPROVED);assertThat(journalRepository.count()).isEqualTo(n+1);assertThat(journalRepository.existsByReference("invoice-approved:"+i.id())).isTrue();assertThat(ledger.balances("INR").stream().map(AccountView::balance).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add)).isEqualByComparingTo("0");}
 @Test void repeatedDecisionDoesNotAccrueTwice(){var i=invoice("10");invoices.decide(i.id(),decision(),"approver",false,true);long n=journalRepository.count();assertThatThrownBy(()->invoices.decide(i.id(),decision(),"approver",false,true)).isInstanceOf(DomainException.class);assertThat(journalRepository.count()).isEqualTo(n);}
}
