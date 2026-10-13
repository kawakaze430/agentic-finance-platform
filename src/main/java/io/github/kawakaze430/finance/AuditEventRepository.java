package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditEventRepository extends JpaRepository<AuditEvent,UUID> {
org.springframework.data.domain.Page<AuditEvent> findByResourceId(UUID resourceId,org.springframework.data.domain.Pageable pageable);
}
