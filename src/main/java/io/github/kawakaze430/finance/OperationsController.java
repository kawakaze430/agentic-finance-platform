package io.github.kawakaze430.finance;

import java.util.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api")
public class OperationsController {
    final DashboardService dashboard;final AuditEventRepository audit;final OutboxEventRepository outbox;final PolicyService policies;
    public OperationsController(DashboardService dashboard,AuditEventRepository audit,OutboxEventRepository outbox,PolicyService policies) { this.dashboard=dashboard;this.audit=audit;this.outbox=outbox;this.policies=policies; }
    @GetMapping("/dashboard") public DashboardView dashboard(@RequestParam(defaultValue="INR") String currency) { return dashboard.summary(currency); }
    @GetMapping("/audit") public PageView<AuditView> audit(@RequestParam(required=false) UUID resourceId,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="25") int size) {
        var p=resourceId==null?audit.findAll(PageLimits.of(page,size)):audit.findByResourceId(resourceId,PageLimits.of(page,size));return PageView.of(p.map(AuditEvent::view));
    }
    @GetMapping("/policies") public List<PolicyDocument> policies(@RequestParam(defaultValue="approval agent") String q) { return policies.search(q); }
    @GetMapping("/outbox") public PageView<Map<String,Object>> outbox(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="25") int size) {
        return PageView.of(outbox.findAll(PageLimits.of(page,size)).map(e->{Map<String,Object> m=new LinkedHashMap<>();m.put("id",e.id);m.put("kind",e.kind);m.put("createdAt",e.createdAt);m.put("publishedAt",e.publishedAt);m.put("attempts",e.attempts);return m;}));
    }
}
