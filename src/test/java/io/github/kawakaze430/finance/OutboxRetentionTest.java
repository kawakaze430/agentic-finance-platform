package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
public class OutboxRetentionTest extends IntegrationSupport {
 @Test void disabledPublisherRetainsUndeliveredEvents(){var i=invoice("10");invoices.decide(i.id(),decision(),"approver",false,true);var events=outboxRepository.findAll().stream().filter(e->e.aggregateId.equals(i.id())).toList();assertThat(events).hasSize(2);assertThat(events).allMatch(e->e.publishedAt==null);}
}
