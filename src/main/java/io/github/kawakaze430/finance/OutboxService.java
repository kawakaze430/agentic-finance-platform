package io.github.kawakaze430.finance;

import java.time.Clock;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
@Service
public class OutboxService {
    final OutboxEventRepository repository; final ObjectMapper json; final Clock clock;
    public OutboxService(OutboxEventRepository repository,ObjectMapper json,Clock clock) { this.repository=repository;this.json=json;this.clock=clock; }
    public void record(String kind,UUID id,Object payload) {
        var e=new OutboxEvent();e.kind=kind;e.aggregateId=id;e.createdAt=clock.instant();
        try { e.payload=json.writeValueAsString(payload); } catch(Exception ex) { throw new IllegalStateException("Could not encode outbox event",ex); }
        repository.save(e);
    }
}
