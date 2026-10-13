package io.github.kawakaze430.finance;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/agent/runs")
public class AgentController {
    final AgentOrchestrator agent;final AgentRunService runs;
    public AgentController(AgentOrchestrator agent,AgentRunService runs) { this.agent=agent;this.runs=runs; }
    @PostMapping public AgentRunView run(@Valid @RequestBody Requests.Agent r,Authentication a) { return agent.run(r,a.getName()); }
    @GetMapping("/{id}") public AgentRunView get(@PathVariable UUID id) { return runs.get(id); }
}
