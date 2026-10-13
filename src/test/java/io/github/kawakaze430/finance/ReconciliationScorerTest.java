package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;import java.time.LocalDate;import java.util.UUID;
public class ReconciliationScorerTest {
 final InvoiceView invoice=new InvoiceView(UUID.randomUUID(),UUID.randomUUID(),"Acme Labs","ACME-001",new BigDecimal("10.00"),"INR",LocalDate.now(),InvoiceStatus.RECEIVED,"maker",0);
 BankTransactionView txn(String ref,String name,String amount,String currency,boolean matched){return new BankTransactionView(UUID.randomUUID(),"bank1",ref,name,new BigDecimal(amount),currency,LocalDate.now(),matched);}
 @Test void exactMatchHasCompleteEvidence(){var s=ReconciliationScorer.score(invoice,txn("ACME-001","Acme Labs","10","INR",false));assertThat(s.score()).isEqualTo(100);assertThat(s.reasons()).hasSize(3);}
 @Test void normalizesFormatting(){assertThat(ReconciliationScorer.score(invoice,txn("acme001","ACME LABS","10","INR",false)).score()).isEqualTo(100);}
 @Test void referenceMismatchCannotSettle(){assertThat(ReconciliationScorer.score(invoice,txn("OTHER","Acme Labs","10","INR",false)).score()).isEqualTo(70);}
 @Test void currencyMismatchNeverMatches(){assertThat(ReconciliationScorer.score(invoice,txn("ACME-001","Acme Labs","10","USD",false)).score()).isZero();}
 @Test void matchedTransactionNeverReused(){assertThat(ReconciliationScorer.score(invoice,txn("ACME-001","Acme Labs","10","INR",true)).score()).isZero();}
}
