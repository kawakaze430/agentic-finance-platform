package io.github.kawakaze430.finance;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/invoices")
public class InvoiceController {
    final InvoiceService service;final IdempotencyService commands;
    public InvoiceController(InvoiceService service,IdempotencyService commands) { this.service=service;this.commands=commands; }
    @GetMapping public PageView<InvoiceView> list(@RequestParam(defaultValue="INR") String currency,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="25") int size) { return service.list(currency,page,size); }
    @GetMapping("/{id}") public InvoiceView get(@PathVariable UUID id) { return service.get(id); }
    @PostMapping public InvoiceView create(@Valid @RequestBody Requests.Invoice r,@RequestHeader("Idempotency-Key") String key,Authentication a) { return commands.execute(a.getName(),key,"invoice:create",r,InvoiceView.class,()->service.create(r,a.getName())); }
    @PostMapping("/{id}/approve") public InvoiceView approve(@PathVariable UUID id,@Valid @RequestBody Requests.Decision r,@RequestHeader("Idempotency-Key") String key,Authentication a) { return decision(id,r,key,a,true); }
    @PostMapping("/{id}/reject") public InvoiceView reject(@PathVariable UUID id,@Valid @RequestBody Requests.Decision r,@RequestHeader("Idempotency-Key") String key,Authentication a) { return decision(id,r,key,a,false); }
    private InvoiceView decision(UUID id,Requests.Decision r,String key,Authentication a,boolean approve) {
        boolean admin=a.getAuthorities().stream().anyMatch(role->role.getAuthority().equals("ROLE_ADMIN"));
        return commands.execute(a.getName(),key,"invoice:"+id+":"+(approve?"approve":"reject"),r,InvoiceView.class,()->service.decide(id,r,a.getName(),admin,approve));
    }
}
