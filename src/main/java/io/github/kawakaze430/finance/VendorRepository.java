package io.github.kawakaze430.finance;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VendorRepository extends JpaRepository<Vendor,UUID> {
java.util.Optional<Vendor> findByName(String name);
}
