package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.List;import java.util.Map;
public class AgentBudgetTest extends IntegrationSupport {
 @Test void repeatedToolRequestsFailWithinBudget(){var i=invoice("10");AgentBrain loop=(invoice,baseline,messages,definitions,iteration)->new BrainReply(List.of(new ToolCall("get_invoice",json.valueToTree(Map.of("invoice_id",invoice.id().toString())))),null);var runner=new AgentOrchestrator(invoices,risk,policies,tools,loop,runs,json,metrics,"demo",2);var result=runner.run(new Requests.Agent(i.id(),"Review"),"analyst");assertThat(result.status()).isEqualTo(AgentStatus.FAILED);assertThat(result.trace()).hasSize(2);assertThat(result.errorCode()).isNotNull();assertThat(invoices.get(i.id()).status()).isEqualTo(InvoiceStatus.RECEIVED);}
 @Test void finalResponseRequiresRetrievedEvidence(){var i=invoice("10");AgentBrain shortcut=(invoice,baseline,messages,definitions,iteration)->new BrainReply(List.of(),baseline);var runner=new AgentOrchestrator(invoices,risk,policies,tools,shortcut,runs,json,metrics,"demo",6);assertThat(runner.run(new Requests.Agent(i.id(),"Review"),"analyst").status()).isEqualTo(AgentStatus.FAILED);}
}
