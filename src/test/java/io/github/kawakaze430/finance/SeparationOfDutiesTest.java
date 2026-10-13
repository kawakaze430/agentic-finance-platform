package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class SeparationOfDutiesTest extends IntegrationSupport {
 @Test void creatorCannotApproveOwnInvoiceEvenAsAdmin(){var i=invoice("10");assertThatThrownBy(()->invoices.decide(i.id(),decision(),"fixture",true,true)).isInstanceOf(DomainException.class).hasMessageContaining("creator");assertThat(invoices.get(i.id()).status()).isEqualTo(InvoiceStatus.RECEIVED);}
 @Test void creatorCannotRejectOwnInvoice(){var i=invoice("10");assertThatThrownBy(()->invoices.decide(i.id(),decision(),"fixture",true,false)).isInstanceOf(DomainException.class);}
}
