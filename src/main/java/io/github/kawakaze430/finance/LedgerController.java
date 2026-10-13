package io.github.kawakaze430.finance;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/ledger")
public class LedgerController {
    final LedgerService service;final IdempotencyService commands;
    public LedgerController(LedgerService service,IdempotencyService commands) { this.service=service;this.commands=commands; }
    @GetMapping("/accounts") public List<AccountView> accounts(@RequestParam(defaultValue="INR") String currency) { return service.balances(currency); }
    @GetMapping("/journals") public PageView<JournalView> journals(@RequestParam(defaultValue="INR") String currency,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="25") int size) { return service.journals(currency,page,size); }
    @PostMapping("/journals") public JournalView post(@Valid @RequestBody Requests.Journal r,@RequestHeader("Idempotency-Key") String key,Authentication a) { return commands.execute(a.getName(),key,"journal:post",r,JournalView.class,()->service.post(r,a.getName())); }
}
