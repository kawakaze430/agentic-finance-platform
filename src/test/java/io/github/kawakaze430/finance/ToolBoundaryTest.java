package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.Map;
public class ToolBoundaryTest extends IntegrationSupport {
 @Test void rejectsFinancialMutationTool(){var i=invoice("10");assertThatThrownBy(()->tools.execute(new ToolCall("approve_invoice",json.valueToTree(Map.of("invoice_id",i.id().toString()))),i)).isInstanceOf(DomainException.class).hasMessageContaining("allowlisted");}
 @Test void cannotReadAnotherInvoice(){var i=invoice("10");var other=invoice("20");assertThatThrownBy(()->tools.execute(new ToolCall("get_invoice",json.valueToTree(Map.of("invoice_id",other.id().toString()))),i)).isInstanceOf(DomainException.class);}
 @Test void rejectsExtraArguments(){var i=invoice("10");assertThatThrownBy(()->tools.execute(new ToolCall("get_invoice",json.valueToTree(Map.of("invoice_id",i.id().toString(),"execute","approve"))),i)).isInstanceOf(DomainException.class);}
}
