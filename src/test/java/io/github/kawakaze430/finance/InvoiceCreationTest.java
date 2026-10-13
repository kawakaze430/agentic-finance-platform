package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;import java.time.LocalDate;
public class InvoiceCreationTest extends IntegrationSupport {
 @Test void createsReceivedInvoiceWithAuditAndOutbox(){long events=outboxRepository.count();var i=invoice("19.95");assertThat(i.status()).isEqualTo(InvoiceStatus.RECEIVED);assertThat(i.amount()).isEqualByComparingTo("19.95");assertThat(outboxRepository.count()).isEqualTo(events+1);assertThat(auditRepository.findByResourceId(i.id(),PageLimits.of(0,25))).extracting(AuditEvent::view).extracting(AuditView::action).contains("INVOICE_RECEIVED");}
 @Test void rejectsVendorCurrencyMismatch(){var v=vendor("USD");assertThatThrownBy(()->invoices.create(new Requests.Invoice(v.id(),key(),BigDecimal.TEN,"INR",LocalDate.now(clock)),"maker")).isInstanceOf(DomainException.class);}
}
