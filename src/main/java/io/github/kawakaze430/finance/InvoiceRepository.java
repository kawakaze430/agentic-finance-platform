package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InvoiceRepository extends JpaRepository<Invoice,UUID> {

@org.springframework.data.jpa.repository.EntityGraph(attributePaths="vendor")
org.springframework.data.domain.Page<Invoice> findByCurrency(String currency,org.springframework.data.domain.Pageable pageable);
@org.springframework.data.jpa.repository.EntityGraph(attributePaths="vendor")
java.util.Optional<Invoice> findByVendorIdAndReference(UUID vendorId,String reference);
@org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
@org.springframework.data.jpa.repository.Query("select i from Invoice i join fetch i.vendor where i.id=:id")
java.util.Optional<Invoice> lockById(@org.springframework.data.repository.query.Param("id") UUID id);
long countByCurrency(String currency);
long countByCurrencyAndStatus(String currency,InvoiceStatus status);
@org.springframework.data.jpa.repository.Query("select coalesce(sum(i.amount),0) from Invoice i where i.currency=:currency and i.status in (io.github.kawakaze430.finance.InvoiceStatus.RECEIVED,io.github.kawakaze430.finance.InvoiceStatus.APPROVED)")
java.math.BigDecimal outstanding(@org.springframework.data.repository.query.Param("currency") String currency);

}
