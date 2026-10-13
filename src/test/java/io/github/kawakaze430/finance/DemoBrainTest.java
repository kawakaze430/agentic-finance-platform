package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;import java.math.BigDecimal;import java.time.*;import java.util.*;
public class DemoBrainTest {
 @Test void retrievesEvidenceBeforeReturningPolicyResult(){var i=new InvoiceView(UUID.randomUUID(),UUID.randomUUID(),"Acme","A1",BigDecimal.TEN,"INR",LocalDate.now(),InvoiceStatus.RECEIVED,"maker",0);var r=new Recommendation(RiskLevel.LOW,"Review",List.of(RecommendationAction.REQUEST_HUMAN_REVIEW),List.of("P-APPROVAL"));var brain=new DemoBrain(new ObjectMapper());assertThat(brain.respond(i,r,List.of(),List.of(),0).calls()).extracting(ToolCall::name).containsExactly("get_invoice","search_policy","reconciliation_candidates");assertThat(brain.respond(i,r,List.of(),List.of(),1).recommendation()).isEqualTo(r);}
}
