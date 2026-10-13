package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BankTransactionRepository extends JpaRepository<BankTransaction,UUID> {

java.util.Optional<BankTransaction> findByExternalId(String externalId);
java.util.List<BankTransaction> findByCurrencyAndMatchedFalseOrderByCreatedAtAsc(String currency);
org.springframework.data.domain.Page<BankTransaction> findByCurrency(String currency,org.springframework.data.domain.Pageable pageable);
long countByCurrencyAndMatchedFalse(String currency);
@org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
@org.springframework.data.jpa.repository.Query("select b from BankTransaction b where b.id=:id")
java.util.Optional<BankTransaction> lockById(@org.springframework.data.repository.query.Param("id") UUID id);

}
