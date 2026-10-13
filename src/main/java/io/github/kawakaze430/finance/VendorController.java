package io.github.kawakaze430.finance;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/vendors")
public class VendorController {
    final VendorService service;final VendorRepository repository;final IdempotencyService commands;
    public VendorController(VendorService service,VendorRepository repository,IdempotencyService commands) { this.service=service;this.repository=repository;this.commands=commands; }
    @GetMapping public PageView<VendorView> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="25") int size) { return PageView.of(repository.findAll(PageLimits.of(page,size)).map(Vendor::view)); }
    @PostMapping public VendorView create(@Valid @RequestBody Requests.Vendor r,@RequestHeader("Idempotency-Key") String key,Authentication a) { return commands.execute(a.getName(),key,"vendor:create",r,VendorView.class,()->service.create(r,a.getName())); }
}
