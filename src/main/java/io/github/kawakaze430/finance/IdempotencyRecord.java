package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="idempotency_record",uniqueConstraints=@UniqueConstraint(columnNames={"actor","request_key"}))
public class IdempotencyRecord {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public IdempotencyRecord() {}

@Column(nullable=false,length=100) String actor;
@Column(name="request_key",nullable=false,length=80) String requestKey;
@Column(nullable=false,length=64) String fingerprint;
@Column(columnDefinition="text") String response;

}
