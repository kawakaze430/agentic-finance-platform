package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="finance_invoice",uniqueConstraints=@UniqueConstraint(columnNames={"vendor_id","reference"}))
public class Invoice {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public Invoice() {}

@ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="vendor_id") Vendor vendor;
@Column(nullable=false,length=80) String reference;
@Column(nullable=false,precision=18,scale=2) java.math.BigDecimal amount;
@Column(nullable=false,length=3) String currency;
@Column(nullable=false) java.time.LocalDate dueDate;
@Enumerated(EnumType.STRING) @Column(nullable=false,length=20) InvoiceStatus status;
@Column(nullable=false,length=100) String createdBy;
@Column(length=500) String decisionReason;
@Version long version;
InvoiceView view() { return new InvoiceView(id,vendor.getId(),vendor.getName(),reference,amount,currency,dueDate,status,createdBy,version); }

}
