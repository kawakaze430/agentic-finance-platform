package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OutboxEventRepository extends JpaRepository<OutboxEvent,UUID> {
@org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
@org.springframework.data.jpa.repository.Query("select e from OutboxEvent e where e.publishedAt is null order by e.createdAt,e.id")
java.util.List<OutboxEvent> lockPending(org.springframework.data.domain.Pageable pageable);
}
