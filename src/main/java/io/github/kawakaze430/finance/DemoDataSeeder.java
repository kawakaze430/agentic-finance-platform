package io.github.kawakaze430.finance;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component @ConditionalOnProperty(name="finance.seed-demo",havingValue="true")
public class DemoDataSeeder implements ApplicationRunner {
    final VendorService vendors;final VendorRepository vendorRepository;final InvoiceService invoices;final InvoiceRepository invoiceRepository;final BankService bank;final BankTransactionRepository bankRepository;final LedgerService ledger;final JournalEntryRepository journals;final Clock clock;
    public DemoDataSeeder(VendorService vendors,VendorRepository vendorRepository,InvoiceService invoices,InvoiceRepository invoiceRepository,BankService bank,BankTransactionRepository bankRepository,LedgerService ledger,JournalEntryRepository journals,Clock clock) { this.vendors=vendors;this.vendorRepository=vendorRepository;this.invoices=invoices;this.invoiceRepository=invoiceRepository;this.bank=bank;this.bankRepository=bankRepository;this.ledger=ledger;this.journals=journals;this.clock=clock; }
    @Override @Transactional public void run(ApplicationArguments arguments) {
        LocalDate today=LocalDate.now(clock);
        var acme=vendorRepository.findByName("Acme Cloud Labs").map(Vendor::view).orElseGet(()->vendors.create(new Requests.Vendor("Acme Cloud Labs","billing@acme.example","INR"),"demo-loader"));
        var nova=vendorRepository.findByName("Nova Packaging").map(Vendor::view).orElseGet(()->vendors.create(new Requests.Vendor("Nova Packaging","finance@nova.example","INR"),"demo-loader"));
        if(!journals.existsByReference("DEMO-OPENING")) ledger.post(new Requests.Journal("DEMO-OPENING","INR","Synthetic opening capital",List.of(new Requests.Line(AccountCode.CASH,new BigDecimal("1000000.00"),BigDecimal.ZERO),new Requests.Line(AccountCode.EQUITY,BigDecimal.ZERO,new BigDecimal("1000000.00")))),"demo-loader");
        if(invoiceRepository.findByVendorIdAndReference(acme.id(),"ACME-2026-1001").isEmpty()) invoices.create(new Requests.Invoice(acme.id(),"ACME-2026-1001",new BigDecimal("18000.00"),"INR",today.plusDays(12)),"demo-loader");
        if(invoiceRepository.findByVendorIdAndReference(nova.id(),"NOVA-2026-2001").isEmpty()) invoices.create(new Requests.Invoice(nova.id(),"NOVA-2026-2001",new BigDecimal("275000.00"),"INR",today.plusDays(7)),"demo-loader");
        if(invoiceRepository.findByVendorIdAndReference(acme.id(),"ACME-2026-1002").isEmpty()) invoices.create(new Requests.Invoice(acme.id(),"ACME-2026-1002",new BigDecimal("4200.00"),"INR",today.minusDays(10)),"demo-loader");
        if(bankRepository.findByExternalId("DEMO-BANK-001").isEmpty()) bank.create(new Requests.BankTransaction("DEMO-BANK-001","ACME-2026-1001","Acme Cloud Labs",new BigDecimal("18000.00"),"INR",today),"demo-loader");
        if(bankRepository.findByExternalId("DEMO-BANK-002").isEmpty()) bank.create(new Requests.BankTransaction("DEMO-BANK-002","UNKNOWN-REFERENCE","Acme Cloud Labs",new BigDecimal("18000.00"),"INR",today),"demo-loader");
    }
}
