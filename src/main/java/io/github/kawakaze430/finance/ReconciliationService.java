package io.github.kawakaze430.finance;

import java.time.Clock;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ReconciliationService {
    final InvoiceRepository invoices;final BankTransactionRepository bank;final ReconciliationMatchRepository matches;final LedgerService ledger;final AuditService audit;final OutboxService outbox;final Clock clock;
    public ReconciliationService(InvoiceRepository invoices,BankTransactionRepository bank,ReconciliationMatchRepository matches,LedgerService ledger,AuditService audit,OutboxService outbox,Clock clock) { this.invoices=invoices;this.bank=bank;this.matches=matches;this.ledger=ledger;this.audit=audit;this.outbox=outbox;this.clock=clock; }
    @Transactional(readOnly=true) public List<ReconciliationCandidate> candidates(UUID invoiceId) {
        var invoice=invoices.findById(invoiceId).orElseThrow(()->DomainException.missing("Invoice"));
        if(invoice.status==InvoiceStatus.PAID || invoice.status==InvoiceStatus.REJECTED) return List.of();
        return bank.findByCurrencyAndMatchedFalseOrderByCreatedAtAsc(invoice.currency).stream().map(b->ReconciliationScorer.score(invoice.view(),b.view())).filter(c->c.score()>=60).sorted(Comparator.comparingInt(ReconciliationCandidate::score).reversed()).limit(25).toList();
    }
    @Transactional public MatchView match(Requests.Match request,String actor) {
        var invoice=invoices.lockById(request.invoiceId()).orElseThrow(()->DomainException.missing("Invoice"));
        if(invoice.status!=InvoiceStatus.APPROVED) throw DomainException.conflict("Settlement requires a human-approved invoice");
        var transaction=bank.lockById(request.bankTransactionId()).orElseThrow(()->DomainException.missing("Bank transaction"));
        if(transaction.matched()) throw DomainException.conflict("Bank transaction is already matched");
        if(ReconciliationScorer.score(invoice.view(),transaction.view()).score()!=100) throw DomainException.invalid("Settlement requires exact amount, currency, invoice reference, and counterparty");
        var match=new ReconciliationMatch();match.invoiceId=invoice.id;match.bankTransactionId=transaction.id;match.matchedBy=actor;match.createdAt=clock.instant();
        matches.saveAndFlush(match);transaction.matched=true;invoice.status=InvoiceStatus.PAID;bank.save(transaction);invoices.saveAndFlush(invoice);ledger.recordSettlement(invoice,actor);
        audit.record("INVOICE_SETTLED",actor,invoice.id);outbox.record("INVOICE_SETTLED",invoice.id,match.view());return match.view();
    }
}
