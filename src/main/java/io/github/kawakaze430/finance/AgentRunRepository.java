package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AgentRunRepository extends JpaRepository<AgentRun,UUID> {
org.springframework.data.domain.Page<AgentRun> findByInvoiceId(UUID invoiceId,org.springframework.data.domain.Pageable pageable);
}
