package io.github.kawakaze430.finance;

import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
@Service
public class AuditService {
    final AuditEventRepository repository; final Clock clock;
    public AuditService(AuditEventRepository repository,Clock clock) { this.repository=repository;this.clock=clock; }
    public void record(String action,String actor,UUID id) {
        var e=new AuditEvent();e.action=action;e.actor=actor;e.resourceId=id;e.createdAt=clock.instant();repository.save(e);
    }
}
