package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="reconciliation_match")
public class ReconciliationMatch {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public ReconciliationMatch() {}

@Column(nullable=false,unique=true) java.util.UUID invoiceId;
@Column(nullable=false,unique=true) java.util.UUID bankTransactionId;
@Column(nullable=false,length=100) String matchedBy;
MatchView view() { return new MatchView(id,invoiceId,bankTransactionId,matchedBy,createdAt); }

}
