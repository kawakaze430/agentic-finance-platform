package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import static org.assertj.core.api.Assertions.*;
public class RequestTransactionBoundaryTest extends IntegrationSupport {
 @Test @Transactional(propagation=Propagation.NOT_SUPPORTED)
 void agentRetrievesVendorAndBankEvidenceAcrossIndependentTransactions() {
  var i=invoice("27.50");bankFor(i);
  var loaded=invoices.get(i.id());assertThat(loaded.vendorId()).isEqualTo(i.vendorId());assertThat(loaded.vendorName()).isEqualTo(i.vendorName());
  var result=agent.run(new Requests.Agent(i.id(),"Review invoice and bank evidence"),"analyst");
  assertThat(result.status()).isEqualTo(AgentStatus.COMPLETED);assertThat(result.trace()).hasSize(3);
  assertThat(result.trace().get(2).result().get(0).path("score").asInt()).isEqualTo(100);
 }
 @Test @Transactional(propagation=Propagation.NOT_SUPPORTED)
 void journalHistoryLoadsAccountCodesWithoutAPreexistingPersistenceContext() {
  var i=invoice("28");invoices.decide(i.id(),decision(),"approver",false,true);
  var journal=ledger.journals("INR",0,100).items().stream().filter(j->j.reference().equals("invoice-approved:"+i.id())).findFirst().orElseThrow();
  assertThat(journal.lines()).extracting(LedgerLineView::account).containsExactlyInAnyOrder(AccountCode.EXPENSE,AccountCode.ACCOUNTS_PAYABLE);
 }
}
