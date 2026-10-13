package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LedgerLineRepository extends JpaRepository<LedgerLine,UUID> {
@org.springframework.data.jpa.repository.Query("select coalesce(sum(l.debit-l.credit),0) from LedgerLine l where l.account.id=:id")
java.math.BigDecimal balance(@org.springframework.data.repository.query.Param("id") UUID id);
}
