package io.github.kawakaze430.finance;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles({"demo","test"}) @Transactional
public abstract class IntegrationSupport {
    @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired Clock clock;
    @Autowired VendorService vendors; @Autowired VendorRepository vendorRepository;
    @Autowired InvoiceService invoices; @Autowired InvoiceRepository invoiceRepository;
    @Autowired LedgerService ledger; @Autowired JournalEntryRepository journalRepository;
    @Autowired BankService bank; @Autowired BankTransactionRepository bankRepository;
    @Autowired ReconciliationService reconciliation; @Autowired ReconciliationMatchRepository matchRepository;
    @Autowired IdempotencyService commands; @Autowired IdempotencyRecordRepository idempotencyRepository;
    @Autowired AuditEventRepository auditRepository; @Autowired OutboxEventRepository outboxRepository;
    @Autowired AgentOrchestrator agent; @Autowired AgentRunService runs; @Autowired AgentRunRepository runRepository;
    @Autowired RiskService risk; @Autowired PolicyService policies; @Autowired ToolRegistry tools;
    @Autowired io.micrometer.core.instrument.MeterRegistry metrics;
    VendorView vendor(String currency) { return vendors.create(new Requests.Vendor("Vendor "+UUID.randomUUID(),"billing@vendor.example",currency),"fixture"); }
    InvoiceView invoice(String amount) { var v=vendor("INR");return invoices.create(new Requests.Invoice(v.id(),"TEST-"+UUID.randomUUID(),new BigDecimal(amount),"INR",LocalDate.now(clock).plusDays(5)),"fixture"); }
    BankTransactionView bankFor(InvoiceView i) { return bank.create(new Requests.BankTransaction(UUID.randomUUID().toString(),i.reference(),i.vendorName(),i.amount(),i.currency(),LocalDate.now(clock)),"fixture"); }
    Requests.Decision decision() { return new Requests.Decision("Reviewed source invoice and bank evidence"); }
    String key() { return UUID.randomUUID().toString(); }
}
