package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class SettlementWorkflowTest extends IntegrationSupport {
 @Test void exactHumanApprovedSettlementPostsJournal(){var i=invoice("50");var b=bankFor(i);invoices.decide(i.id(),decision(),"approver",false,true);var match=reconciliation.match(new Requests.Match(i.id(),b.id()),"approver");assertThat(match.invoiceId()).isEqualTo(i.id());assertThat(invoices.get(i.id()).status()).isEqualTo(InvoiceStatus.PAID);assertThat(bankRepository.findById(b.id()).orElseThrow().matched).isTrue();assertThat(journalRepository.existsByReference("invoice-settled:"+i.id())).isTrue();}
 @Test void receivedInvoiceCannotSettle(){var i=invoice("10");var b=bankFor(i);assertThatThrownBy(()->reconciliation.match(new Requests.Match(i.id(),b.id()),"approver")).isInstanceOf(DomainException.class);assertThat(bankRepository.findById(b.id()).orElseThrow().matched).isFalse();}
}
