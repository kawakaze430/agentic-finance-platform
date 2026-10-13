package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="bank_transaction")
public class BankTransaction {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public BankTransaction() {}

@Column(nullable=false,unique=true,length=100) String externalId;
@Column(nullable=false,length=120) String reference;
@Column(nullable=false,length=120) String counterparty;
@Column(nullable=false,precision=18,scale=2) java.math.BigDecimal amount;
@Column(nullable=false,length=3) String currency;
@Column(nullable=false) java.time.LocalDate bookedOn;
@Column(nullable=false) boolean matched;
@Version long version;
BankTransactionView view() { return new BankTransactionView(id,externalId,reference,counterparty,amount,currency,bookedOn,matched); }

}
