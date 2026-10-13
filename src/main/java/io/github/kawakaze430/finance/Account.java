package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="ledger_account",uniqueConstraints=@UniqueConstraint(columnNames={"code","currency"}))
public class Account {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public Account() {}
public AccountCode getCode() { return code; }

@Enumerated(EnumType.STRING) @Column(nullable=false,length=30) AccountCode code;
@Column(nullable=false,length=3) String currency;

}
