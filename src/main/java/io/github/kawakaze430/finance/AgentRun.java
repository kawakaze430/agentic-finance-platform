package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="agent_run")
public class AgentRun {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public AgentRun() {}

@Column(nullable=false) java.util.UUID invoiceId;
@Column(nullable=false) long invoiceVersion;
@Column(nullable=false,length=20) String mode;
@Enumerated(EnumType.STRING) @Column(nullable=false,length=20) AgentStatus status;
@Column(columnDefinition="text") String recommendation;
@Column(nullable=false,columnDefinition="text") String trace = "[]";
@Column(length=80) String errorCode;

}
