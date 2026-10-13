package io.github.kawakaze430.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
public class DemoBrain implements AgentBrain {
    final ObjectMapper json;
    public DemoBrain(ObjectMapper json) { this.json=json; }
    @Override public BrainReply respond(InvoiceView invoice,Recommendation baseline,List<Map<String,Object>> messages,List<Map<String,Object>> tools,int iteration) {
        if(iteration==0) return new BrainReply(List.of(
            new ToolCall("get_invoice",json.valueToTree(Map.of("invoice_id",invoice.id().toString()))),
            new ToolCall("search_policy",json.valueToTree(Map.of("query","approval bank reconciliation agent due date"))),
            new ToolCall("reconciliation_candidates",json.valueToTree(Map.of("invoice_id",invoice.id().toString())))),null);
        return new BrainReply(List.of(),baseline);
    }
}
