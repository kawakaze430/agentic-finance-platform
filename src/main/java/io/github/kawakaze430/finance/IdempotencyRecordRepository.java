package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord,UUID> {
java.util.Optional<IdempotencyRecord> findByActorAndRequestKey(String actor,String requestKey);
}
