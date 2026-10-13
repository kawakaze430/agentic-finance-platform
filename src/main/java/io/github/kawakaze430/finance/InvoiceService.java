package io.github.kawakaze430.finance;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class InvoiceService {
    final InvoiceRepository repository;final VendorRepository vendors;final LedgerService ledger;final AuditService audit;final OutboxService outbox;final Clock clock;final BigDecimal limit;
    public InvoiceService(InvoiceRepository repository,VendorRepository vendors,LedgerService ledger,AuditService audit,OutboxService outbox,Clock clock,@Value("${finance.approval-limit}") BigDecimal limit) {
        this.repository=repository;this.vendors=vendors;this.ledger=ledger;this.audit=audit;this.outbox=outbox;this.clock=clock;this.limit=limit;
    }
    @Transactional public InvoiceView create(Requests.Invoice request,String actor) {
        var vendor=vendors.findById(request.vendorId()).orElseThrow(()->DomainException.missing("Vendor"));
        var money=Money.positive(request.amount(),request.currency());
        if(!vendor.currency.equals(money.currency())) throw DomainException.invalid("Invoice currency must match the vendor");
        if(repository.findByVendorIdAndReference(vendor.id,request.reference()).isPresent()) throw DomainException.conflict("Vendor invoice reference already exists");
        var invoice=new Invoice();invoice.vendor=vendor;invoice.reference=request.reference();invoice.amount=money.amount();invoice.currency=money.currency();invoice.dueDate=request.dueDate();invoice.status=InvoiceStatus.RECEIVED;invoice.createdBy=actor;invoice.createdAt=clock.instant();
        repository.saveAndFlush(invoice);audit.record("INVOICE_RECEIVED",actor,invoice.id);outbox.record("INVOICE_RECEIVED",invoice.id,invoice.view());return invoice.view();
    }
    @Transactional(readOnly=true) public InvoiceView get(UUID id) { return repository.findById(id).orElseThrow(()->DomainException.missing("Invoice")).view(); }
    @Transactional(readOnly=true) public PageView<InvoiceView> list(String currency,int page,int size) { return PageView.of(repository.findByCurrency(Money.normalizeCurrency(currency),PageLimits.of(page,size)).map(Invoice::view)); }
    @Transactional public InvoiceView decide(UUID id,Requests.Decision decision,String actor,boolean administrator,boolean approve) {
        var invoice=repository.lockById(id).orElseThrow(()->DomainException.missing("Invoice"));
        if(invoice.status!=InvoiceStatus.RECEIVED) throw DomainException.conflict("Only received invoices can be approved or rejected");
        if(invoice.createdBy.equals(actor)) throw new DomainException(HttpStatus.FORBIDDEN,"SEPARATION_OF_DUTIES","Invoice creator cannot decide the same invoice");
        if(approve && invoice.amount.compareTo(limit)>0 && !administrator) throw new DomainException(HttpStatus.FORBIDDEN,"ADMIN_REVIEW_REQUIRED","Amount exceeds the approval limit; an administrator must review");
        if(decision.reason()==null || decision.reason().trim().length()<8) throw DomainException.invalid("Provide a review reason of at least eight characters");
        invoice.status=approve?InvoiceStatus.APPROVED:InvoiceStatus.REJECTED;invoice.decisionReason=decision.reason().trim();
        if(approve) ledger.recordInvoice(invoice,actor);
        repository.saveAndFlush(invoice);audit.record(approve?"INVOICE_APPROVED":"INVOICE_REJECTED",actor,id);outbox.record(approve?"INVOICE_APPROVED":"INVOICE_REJECTED",id,invoice.view());return invoice.view();
    }
}
