package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;import java.time.LocalDate;
public class SettlementEvidenceTest extends IntegrationSupport {
 @Test void wrongReferenceCannotSettle(){var i=invoice("50");invoices.decide(i.id(),decision(),"approver",false,true);var b=bank.create(new Requests.BankTransaction(key(),"wrong-reference",i.vendorName(),i.amount(),"INR",LocalDate.now(clock)),"fixture");assertThatThrownBy(()->reconciliation.match(new Requests.Match(i.id(),b.id()),"approver")).isInstanceOf(DomainException.class).hasMessageContaining("exact amount");assertThat(invoices.get(i.id()).status()).isEqualTo(InvoiceStatus.APPROVED);}
 @Test void wrongAmountCannotSettle(){var i=invoice("50");invoices.decide(i.id(),decision(),"approver",false,true);var b=bank.create(new Requests.BankTransaction(key(),i.reference(),i.vendorName(),BigDecimal.ONE,"INR",LocalDate.now(clock)),"fixture");assertThatThrownBy(()->reconciliation.match(new Requests.Match(i.id(),b.id()),"approver")).isInstanceOf(DomainException.class);}
}
