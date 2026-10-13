package io.github.kawakaze430.finance;

import com.fasterxml.jackson.databind.*;
import java.util.*;
import org.springframework.stereotype.Component;
@Component
public class ToolRegistry {
    final PolicyService policies;final LedgerService ledger;final ReconciliationService reconciliation;final ObjectMapper json;
    public ToolRegistry(PolicyService policies,LedgerService ledger,ReconciliationService reconciliation,ObjectMapper json) { this.policies=policies;this.ledger=ledger;this.reconciliation=reconciliation;this.json=json; }
    public List<Map<String,Object>> definitions() {
        return List.of(definition("get_invoice","Read the current run's invoice snapshot","invoice_id"),
            definition("search_policy","Retrieve policy passages using a lexical query","query"),
            definition("reconciliation_candidates","Read possible outgoing bank matches; never settle","invoice_id"),
            definition("ledger_summary","Read balances for the invoice currency","currency"));
    }
    private Map<String,Object> definition(String name,String description,String argument) {
        return Map.of("type","function","function",Map.of("name",name,"description",description,"parameters",Map.of(
            "type","object","properties",Map.of(argument,Map.of("type","string")),"required",List.of(argument),"additionalProperties",false)));
    }
    public JsonNode execute(ToolCall call,InvoiceView invoice) {
        if(call==null || call.name()==null || call.arguments()==null || !call.arguments().isObject()) throw DomainException.invalid("Invalid tool invocation");
        String field=switch(call.name()) {
            case "get_invoice","reconciliation_candidates" -> "invoice_id";
            case "search_policy" -> "query";
            case "ledger_summary" -> "currency";
            default -> throw DomainException.invalid("Tool is not allowlisted");
        };
        if(call.arguments().size()!=1 || !call.arguments().path(field).isTextual()) throw DomainException.invalid("Tool argument does not match its schema");
        String value=call.arguments().path(field).asText();
        if(field.equals("invoice_id") && !value.equals(invoice.id().toString())) throw DomainException.invalid("Tools are scoped to the run's invoice");
        return switch(call.name()) {
            case "get_invoice" -> json.valueToTree(invoice);
            case "search_policy" -> json.valueToTree(policies.search(value));
            case "reconciliation_candidates" -> json.valueToTree(reconciliation.candidates(invoice.id()));
            case "ledger_summary" -> {
                if(!value.equals(invoice.currency())) throw DomainException.invalid("Ledger tools are scoped to the invoice currency");
                yield json.valueToTree(ledger.balances(value));
            }
            default -> throw DomainException.invalid("Tool is not allowlisted");
        };
    }
}
