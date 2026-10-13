package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class PolicyRetrievalTest {
 final PolicyService service=new PolicyService();
 @Test void retrievesSettlementPolicy(){assertThat(service.search("bank reconciliation")).extracting(PolicyDocument::id).contains("P-RECONCILE");}
 @Test void unknownTermsFallBackToAgentBoundary(){assertThat(service.search("unrelated" )).extracting(PolicyDocument::id).containsExactly("P-AGENT");}
 @Test void doesNotInterpretInstructionsAsExecutableCode(){assertThat(service.search("drop table invoices")).isNotEmpty();}
 @Test void boundsQueries(){assertThatThrownBy(()->service.search("x".repeat(1501))).isInstanceOf(DomainException.class);}
}
