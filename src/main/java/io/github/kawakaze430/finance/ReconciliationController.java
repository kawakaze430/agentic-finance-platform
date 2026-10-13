package io.github.kawakaze430.finance;

import jakarta.validation.Valid;
import java.util.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/reconciliation")
public class ReconciliationController {
    final BankService bank;final ReconciliationService service;final IdempotencyService commands;
    public ReconciliationController(BankService bank,ReconciliationService service,IdempotencyService commands) { this.bank=bank;this.service=service;this.commands=commands; }
    @GetMapping("/transactions") public PageView<BankTransactionView> transactions(@RequestParam(defaultValue="INR") String currency,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="25") int size) { return bank.list(currency,page,size); }
    @PostMapping("/transactions") public BankTransactionView importRecord(@Valid @RequestBody Requests.BankTransaction r,@RequestHeader("Idempotency-Key") String key,Authentication a) { return commands.execute(a.getName(),key,"bank:import",r,BankTransactionView.class,()->bank.create(r,a.getName())); }
    @GetMapping("/candidates") public List<ReconciliationCandidate> candidates(@RequestParam UUID invoiceId) { return service.candidates(invoiceId); }
    @PostMapping("/matches") public MatchView match(@Valid @RequestBody Requests.Match r,@RequestHeader("Idempotency-Key") String key,Authentication a) { return commands.execute(a.getName(),key,"reconciliation:match",r,MatchView.class,()->service.match(r,a.getName())); }
}
