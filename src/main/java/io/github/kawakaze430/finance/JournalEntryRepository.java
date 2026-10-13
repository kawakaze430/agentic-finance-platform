package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JournalEntryRepository extends JpaRepository<JournalEntry,UUID> {
boolean existsByReference(String reference);
org.springframework.data.domain.Page<JournalEntry> findByCurrency(String currency,org.springframework.data.domain.Pageable pageable);

}
