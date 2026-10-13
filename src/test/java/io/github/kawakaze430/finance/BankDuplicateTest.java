package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.time.LocalDate;
public class BankDuplicateTest extends IntegrationSupport {
 @Test void duplicateExternalIdsCannotImportTwice(){var i=invoice("10");var b=bankFor(i);assertThatThrownBy(()->bank.create(new Requests.BankTransaction(b.externalId(),b.reference(),b.counterparty(),b.amount(),b.currency(),LocalDate.now(clock)),"fixture")).isInstanceOf(DomainException.class);}
 @Test void closedInvoicesHaveNoCandidates(){var i=invoice("10");bankFor(i);invoices.decide(i.id(),decision(),"approver",false,false);assertThat(reconciliation.candidates(i.id())).isEmpty();}
}
