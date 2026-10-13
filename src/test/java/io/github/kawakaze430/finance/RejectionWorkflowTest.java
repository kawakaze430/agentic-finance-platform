package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class RejectionWorkflowTest extends IntegrationSupport {
 @Test void rejectionDoesNotPostAccrual(){var i=invoice("10");long n=journalRepository.count();assertThat(invoices.decide(i.id(),decision(),"approver",false,false).status()).isEqualTo(InvoiceStatus.REJECTED);assertThat(journalRepository.count()).isEqualTo(n);}
 @Test void rejectedInvoiceCannotReenterApproval(){var i=invoice("10");invoices.decide(i.id(),decision(),"approver",false,false);assertThatThrownBy(()->invoices.decide(i.id(),decision(),"admin",true,true)).isInstanceOf(DomainException.class);}
}
