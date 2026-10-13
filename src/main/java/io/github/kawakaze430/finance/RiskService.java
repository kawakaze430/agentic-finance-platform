package io.github.kawakaze430.finance;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class RiskService {
    final BigDecimal limit;final Clock clock;
    public RiskService(@Value("${finance.approval-limit}") BigDecimal limit,Clock clock) { this.limit=limit;this.clock=clock; }
    public Recommendation assess(InvoiceView invoice) {
        if(invoice.status()==InvoiceStatus.PAID || invoice.status()==InvoiceStatus.REJECTED) return new Recommendation(RiskLevel.LOW,"Invoice is closed; no further approval or settlement is permitted.",List.of(RecommendationAction.NO_ACTION),List.of("P-AGENT"));
        if(invoice.amount().compareTo(limit)>0) return new Recommendation(RiskLevel.HIGH,"Invoice exceeds the configured approval limit of "+limit+" "+invoice.currency()+". An administrator must review it.",List.of(RecommendationAction.REQUEST_ADMIN_REVIEW),List.of("P-APPROVAL","P-AGENT"));
        if(invoice.dueDate().isBefore(LocalDate.now(clock))) return new Recommendation(RiskLevel.MEDIUM,"Invoice is overdue. Review the due date and bank evidence before any decision.",List.of(RecommendationAction.REQUEST_HUMAN_REVIEW),List.of("P-OVERDUE","P-APPROVAL"));
        return new Recommendation(RiskLevel.LOW,"Review invoice details and exact bank evidence. The agent has no authority to approve or settle.",List.of(RecommendationAction.REQUEST_HUMAN_REVIEW),List.of("P-APPROVAL","P-RECONCILE"));
    }
    public Recommendation enforce(Recommendation baseline,Recommendation proposed,PolicyService policies) {
        if(proposed==null || proposed.risk()==null || proposed.summary()==null || proposed.summary().isBlank() || proposed.summary().length()>2000 || proposed.actions()==null || proposed.actions().isEmpty() || proposed.actions().size()>4 || proposed.actions().stream().anyMatch(Objects::isNull) || proposed.policyCitations()==null || proposed.policyCitations().size()>8 || proposed.policyCitations().stream().anyMatch(id->!policies.knownCitation(id)))
            throw DomainException.invalid("Model returned an invalid or uncited recommendation");
        var risk=proposed.risk().ordinal()<baseline.risk().ordinal()?baseline.risk():proposed.risk();
        var actions=new LinkedHashSet<>(baseline.actions());actions.addAll(proposed.actions());
        var citations=new LinkedHashSet<>(baseline.policyCitations());citations.addAll(proposed.policyCitations());
        return new Recommendation(risk,proposed.summary(),List.copyOf(actions),List.copyOf(citations));
    }
}
