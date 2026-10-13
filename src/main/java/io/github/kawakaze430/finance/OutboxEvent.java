package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="outbox_event")
public class OutboxEvent {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public OutboxEvent() {}

@Column(nullable=false,length=80) String kind;
@Column(nullable=false) java.util.UUID aggregateId;
@Column(nullable=false,columnDefinition="text") String payload;
java.time.Instant publishedAt;
@Column(nullable=false) int attempts;

}
