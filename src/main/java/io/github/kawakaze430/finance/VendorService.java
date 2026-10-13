package io.github.kawakaze430.finance;

import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class VendorService {
    final VendorRepository repository; final AuditService audit; final Clock clock;
    public VendorService(VendorRepository repository,AuditService audit,Clock clock) { this.repository=repository;this.audit=audit;this.clock=clock; }
    @Transactional public VendorView create(Requests.Vendor request,String actor) {
        var v=new Vendor();v.name=request.name().trim();v.email=request.email().trim();v.currency=Money.normalizeCurrency(request.currency());v.createdAt=clock.instant();
        repository.save(v);audit.record("VENDOR_CREATED",actor,v.id);return v.view();
    }
}
