package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="journal_entry")
public class JournalEntry {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public JournalEntry() {}

@Column(nullable=false,unique=true,length=120) String reference;
@Column(nullable=false,length=3) String currency;
@Column(nullable=false,length=300) String description;
@OneToMany(mappedBy="journal",cascade=CascadeType.ALL,fetch=FetchType.LAZY) java.util.List<LedgerLine> lines = new java.util.ArrayList<>();
JournalView view() { return new JournalView(id,reference,currency,description,createdAt,lines.stream().map(l->new LedgerLineView(l.account.getCode(),l.debit,l.credit)).toList()); }

}
