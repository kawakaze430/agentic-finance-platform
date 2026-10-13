package io.github.kawakaze430.finance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class DashboardService {
    final InvoiceRepository invoices;final BankTransactionRepository bank;final String mode;
    public DashboardService(InvoiceRepository invoices,BankTransactionRepository bank,@Value("${finance.agent.mode}") String mode) { this.invoices=invoices;this.bank=bank;this.mode=mode; }
    @Transactional(readOnly=true) public DashboardView summary(String value) {
        String currency=Money.normalizeCurrency(value);
        return new DashboardView(currency,invoices.countByCurrency(currency),invoices.countByCurrencyAndStatus(currency,InvoiceStatus.RECEIVED),invoices.countByCurrencyAndStatus(currency,InvoiceStatus.PAID),invoices.outstanding(currency).setScale(2),bank.countByCurrencyAndMatchedFalse(currency),mode);
    }
}
