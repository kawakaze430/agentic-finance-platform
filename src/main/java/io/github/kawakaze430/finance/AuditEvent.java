package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="audit_event")
public class AuditEvent {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public AuditEvent() {}

@Column(nullable=false,length=80) String action;
@Column(nullable=false,length=100) String actor;
@Column(nullable=false) java.util.UUID resourceId;
AuditView view() { return new AuditView(id,action,actor,resourceId,createdAt); }

}
