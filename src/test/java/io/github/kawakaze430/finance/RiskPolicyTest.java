package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;import java.time.*;import java.util.*;
public class RiskPolicyTest {
 final Clock clock=Clock.fixed(Instant.parse("2026-10-13T00:00:00Z"),ZoneOffset.UTC);
 final RiskService risk=new RiskService(new BigDecimal("250000"),clock);
 InvoiceView invoice(String amount,LocalDate due){return new InvoiceView(UUID.randomUUID(),UUID.randomUUID(),"Acme","A1",new BigDecimal(amount),"INR",due,InvoiceStatus.RECEIVED,"maker",0);}
 @Test void highValueNeedsAdmin(){assertThat(risk.assess(invoice("250000.01",LocalDate.of(2026,10,20))).actions()).contains(RecommendationAction.REQUEST_ADMIN_REVIEW);}
 @Test void overdueIsMediumRisk(){assertThat(risk.assess(invoice("10",LocalDate.of(2026,10,12))).risk()).isEqualTo(RiskLevel.MEDIUM);}
 @Test void modelCannotReduceRiskFloor(){var baseline=risk.assess(invoice("300000",LocalDate.of(2026,10,20)));var model=new Recommendation(RiskLevel.LOW,"Everything looks fine",List.of(RecommendationAction.NO_ACTION),List.of("P-AGENT"));var result=risk.enforce(baseline,model,new PolicyService());assertThat(result.risk()).isEqualTo(RiskLevel.HIGH);assertThat(result.actions()).contains(RecommendationAction.REQUEST_ADMIN_REVIEW);}
 @Test void rejectsFabricatedCitation(){var base=risk.assess(invoice("10",LocalDate.of(2026,10,20)));var model=new Recommendation(RiskLevel.LOW,"Review",List.of(RecommendationAction.REQUEST_HUMAN_REVIEW),List.of("P-FAKE"));assertThatThrownBy(()->risk.enforce(base,model,new PolicyService())).isInstanceOf(DomainException.class);}
}
