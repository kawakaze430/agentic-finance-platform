package io.github.kawakaze430.finance;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component @ConditionalOnProperty(name="finance.outbox.enabled",havingValue="true")
public class OutboxPublisher {
    final OutboxEventRepository repository;final KafkaTemplate<String,String> kafka;final ObjectMapper json;final Clock clock;final String topic;
    public OutboxPublisher(OutboxEventRepository repository,KafkaTemplate<String,String> kafka,ObjectMapper json,Clock clock,@Value("${finance.outbox.topic}") String topic) { this.repository=repository;this.kafka=kafka;this.json=json;this.clock=clock;this.topic=topic; }
    @Scheduled(fixedDelayString="${finance.outbox.interval-ms}") @Transactional
    public void publishBatch() {
        for(var event:repository.lockPending(PageRequest.of(0,10))) {
            event.attempts++;
            try {
                var envelope=Map.of("eventId",event.id,"kind",event.kind,"aggregateId",event.aggregateId,"occurredAt",event.createdAt,"payload",json.readTree(event.payload));
                kafka.send(topic,event.aggregateId.toString(),json.writeValueAsString(envelope)).get(8,TimeUnit.SECONDS);
                event.publishedAt=clock.instant();repository.save(event);
            } catch(Exception e) {
                if(e instanceof InterruptedException) Thread.currentThread().interrupt();
                repository.save(event);break;
            }
        }
    }
}
