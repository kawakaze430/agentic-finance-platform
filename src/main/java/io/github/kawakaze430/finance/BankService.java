package io.github.kawakaze430.finance;

import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class BankService {
    final BankTransactionRepository repository;final AuditService audit;final Clock clock;
    public BankService(BankTransactionRepository repository,AuditService audit,Clock clock) { this.repository=repository;this.audit=audit;this.clock=clock; }
    @Transactional public BankTransactionView create(Requests.BankTransaction request,String actor) {
        if(repository.findByExternalId(request.externalId()).isPresent()) throw DomainException.conflict("Bank transaction external ID already exists");
        var m=Money.positive(request.amount(),request.currency());
        var b=new BankTransaction();b.externalId=request.externalId();b.reference=request.reference();b.counterparty=request.counterparty().trim();b.amount=m.amount();b.currency=m.currency();b.bookedOn=request.bookedOn();b.createdAt=clock.instant();
        repository.saveAndFlush(b);audit.record("BANK_TRANSACTION_IMPORTED",actor,b.id);return b.view();
    }
    @Transactional(readOnly=true) public PageView<BankTransactionView> list(String currency,int page,int size) { return PageView.of(repository.findByCurrency(Money.normalizeCurrency(currency),PageLimits.of(page,size)).map(BankTransaction::view)); }
}
