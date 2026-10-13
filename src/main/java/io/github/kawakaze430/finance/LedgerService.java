package io.github.kawakaze430.finance;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class LedgerService {
    final AccountRepository accounts;final JournalEntryRepository journals;final LedgerLineRepository lines;final AuditService audit;final Clock clock;
    public LedgerService(AccountRepository accounts,JournalEntryRepository journals,LedgerLineRepository lines,AuditService audit,Clock clock) { this.accounts=accounts;this.journals=journals;this.lines=lines;this.audit=audit;this.clock=clock; }
    @Transactional public JournalView post(Requests.Journal request,String actor) {
        String currency=Money.normalizeCurrency(request.currency());
        if(journals.existsByReference(request.reference())) throw DomainException.conflict("Journal reference is already posted");
        if(request.lines()==null || request.lines().size()<2 || request.lines().size()>20) throw DomainException.invalid("A journal needs 2 to 20 lines");
        BigDecimal debit=BigDecimal.ZERO,credit=BigDecimal.ZERO;Set<AccountCode> distinct=new HashSet<>();
        for(var line:request.lines()) {
            var d=new Money(line.debit(),currency).amount();var c=new Money(line.credit(),currency).amount();
            if(line.account()==null || (d.signum()>0)==(c.signum()>0)) throw DomainException.invalid("Each journal line must contain exactly one debit or credit");
            debit=debit.add(d);credit=credit.add(c);distinct.add(line.account());
        }
        if(debit.compareTo(credit)!=0 || distinct.size()<2) throw DomainException.invalid("Journal must balance across at least two accounts");
        var entry=new JournalEntry();entry.reference=request.reference();entry.currency=currency;entry.description=request.description();entry.createdAt=clock.instant();
        for(var line:request.lines()) {
            Account account=accounts.findByCodeAndCurrency(line.account(),currency).orElseGet(()->{
                var a=new Account();a.code=line.account();a.currency=currency;a.createdAt=clock.instant();return accounts.save(a);
            });
            var l=new LedgerLine();l.account=account;l.journal=entry;l.debit=line.debit().setScale(2);l.credit=line.credit().setScale(2);entry.lines.add(l);
        }
        journals.saveAndFlush(entry);audit.record("JOURNAL_POSTED",actor,entry.id);return entry.view();
    }
    @Transactional public void recordInvoice(Invoice invoice,String actor) {
        post(new Requests.Journal("invoice-approved:"+invoice.id,invoice.currency,"Accrue invoice "+invoice.reference,List.of(
            new Requests.Line(AccountCode.EXPENSE,invoice.amount,BigDecimal.ZERO),new Requests.Line(AccountCode.ACCOUNTS_PAYABLE,BigDecimal.ZERO,invoice.amount))),actor);
    }
    @Transactional public void recordSettlement(Invoice invoice,String actor) {
        post(new Requests.Journal("invoice-settled:"+invoice.id,invoice.currency,"Record bank settlement "+invoice.reference,List.of(
            new Requests.Line(AccountCode.ACCOUNTS_PAYABLE,invoice.amount,BigDecimal.ZERO),new Requests.Line(AccountCode.CASH,BigDecimal.ZERO,invoice.amount))),actor);
    }
    @Transactional(readOnly=true) public List<AccountView> balances(String currency) {
        return accounts.findByCurrencyOrderByCode(Money.normalizeCurrency(currency)).stream().map(a->new AccountView(a.id,a.code,a.currency,lines.balance(a.id).setScale(2))).toList();
    }
    @Transactional(readOnly=true) public PageView<JournalView> journals(String currency,int page,int size) {
        return PageView.of(journals.findByCurrency(Money.normalizeCurrency(currency),PageLimits.of(page,size)).map(JournalEntry::view));
    }
}
