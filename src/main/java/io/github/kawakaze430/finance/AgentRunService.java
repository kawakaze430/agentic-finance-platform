package io.github.kawakaze430.finance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AgentRunService {
    final AgentRunRepository repository;final ObjectMapper json;final Clock clock;final AuditService audit;
    public AgentRunService(AgentRunRepository repository,ObjectMapper json,Clock clock,AuditService audit) { this.repository=repository;this.json=json;this.clock=clock;this.audit=audit; }
    @Transactional public UUID start(InvoiceView invoice,String mode) {
        var run=new AgentRun();run.invoiceId=invoice.id();run.invoiceVersion=invoice.version();run.mode=mode;run.status=AgentStatus.RUNNING;run.createdAt=clock.instant();repository.save(run);return run.id;
    }
    @Transactional public AgentRunView finish(UUID id,Recommendation recommendation,List<ToolStep> trace,String error,String actor) {
        var run=repository.findById(id).orElseThrow(()->DomainException.missing("Agent run"));
        try { run.trace=json.writeValueAsString(trace);run.recommendation=recommendation==null?null:json.writeValueAsString(recommendation); }
        catch(Exception e) { throw new IllegalStateException("Could not encode agent trace",e); }
        run.status=error==null?AgentStatus.COMPLETED:AgentStatus.FAILED;run.errorCode=error;repository.save(run);
        audit.record(error==null?"AGENT_RECOMMENDATION":"AGENT_FAILED",actor,run.invoiceId);return view(run);
    }
    @Transactional(readOnly=true) public AgentRunView get(UUID id) { return view(repository.findById(id).orElseThrow(()->DomainException.missing("Agent run"))); }
    private AgentRunView view(AgentRun run) {
        try { return new AgentRunView(run.id,run.invoiceId,run.invoiceVersion,run.mode,run.status,run.createdAt,run.recommendation==null?null:json.readValue(run.recommendation,Recommendation.class),json.readValue(run.trace,new TypeReference<List<ToolStep>>(){}),run.errorCode); }
        catch(Exception e) { throw new IllegalStateException("Stored agent trace is invalid",e); }
    }
}
