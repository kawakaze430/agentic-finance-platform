package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.concurrent.CompletableFuture;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.*;
public class OutboxFailureTest extends IntegrationSupport {
 @Test @SuppressWarnings("unchecked") void failedBrokerAcknowledgementLeavesEventPending() {
  invoice("10");long before=outboxRepository.findAll().stream().mapToLong(e->e.attempts).sum();
  KafkaTemplate<String,String> kafka=mock(KafkaTemplate.class);
  when(kafka.send(anyString(),anyString(),anyString())).thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker unavailable")));
  new OutboxPublisher(outboxRepository,kafka,json,clock,"test-events").publishBatch();
  assertThat(outboxRepository.findAll().stream().mapToLong(e->e.attempts).sum()).isEqualTo(before+1);
  assertThat(outboxRepository.findAll()).allMatch(e->e.publishedAt==null);
 }
}
