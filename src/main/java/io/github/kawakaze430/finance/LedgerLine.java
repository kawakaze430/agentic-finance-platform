package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
@Entity @Table(name="ledger_line")
public class LedgerLine {
    @Id UUID id = UUID.randomUUID();
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="journal_id") JournalEntry journal;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="account_id") Account account;
    @Column(nullable=false,precision=18,scale=2) BigDecimal debit;
    @Column(nullable=false,precision=18,scale=2) BigDecimal credit;
    public LedgerLine() {}
}
