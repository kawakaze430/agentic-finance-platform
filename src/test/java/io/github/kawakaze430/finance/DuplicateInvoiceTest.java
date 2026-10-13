package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.time.LocalDate;
public class DuplicateInvoiceTest extends IntegrationSupport {
 @Test void duplicateVendorReferenceCannotCreateAnotherInvoice(){var i=invoice("10");long n=invoiceRepository.count();assertThatThrownBy(()->invoices.create(new Requests.Invoice(i.vendorId(),i.reference(),i.amount(),i.currency(),LocalDate.now(clock)),"maker")).isInstanceOf(DomainException.class);assertThat(invoiceRepository.count()).isEqualTo(n);}
 @Test void sameReferenceCanBelongToDifferentVendor(){var i=invoice("10");var v=vendor("INR");assertThat(invoices.create(new Requests.Invoice(v.id(),i.reference(),i.amount(),i.currency(),LocalDate.now(clock)),"maker").id()).isNotEqualTo(i.id());}
}
