package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AccountRepository extends JpaRepository<Account,UUID> {
java.util.Optional<Account> findByCodeAndCurrency(AccountCode code,String currency); java.util.List<Account> findByCurrencyOrderByCode(String currency);
}
