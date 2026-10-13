package io.github.kawakaze430.finance;

import java.util.List;
import java.util.Map;
public interface AgentBrain {
    BrainReply respond(InvoiceView invoice,Recommendation baseline,List<Map<String,Object>> messages,List<Map<String,Object>> tools,int iteration);
}
