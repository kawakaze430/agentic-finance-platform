package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReconciliationMatchRepository extends JpaRepository<ReconciliationMatch,UUID> {
java.util.Optional<ReconciliationMatch> findByInvoiceId(UUID invoiceId);
}
