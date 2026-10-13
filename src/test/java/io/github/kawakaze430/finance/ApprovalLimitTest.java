package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class ApprovalLimitTest extends IntegrationSupport {
 @Test void approverCannotApproveAboveLimit(){var i=invoice("250000.01");assertThatThrownBy(()->invoices.decide(i.id(),decision(),"approver",false,true)).isInstanceOf(DomainException.class).hasMessageContaining("administrator");}
 @Test void administratorCanReviewAboveLimit(){var i=invoice("300000");assertThat(invoices.decide(i.id(),decision(),"admin",true,true).status()).isEqualTo(InvoiceStatus.APPROVED);}
 @Test void exactLimitIsPermitted(){var i=invoice("250000");assertThat(invoices.decide(i.id(),decision(),"approver",false,true).status()).isEqualTo(InvoiceStatus.APPROVED);}
}
