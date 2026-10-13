package io.github.kawakaze430.finance;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;
public final class ReconciliationScorer {
    private ReconciliationScorer() {}
    static String normalize(String value) { return Normalizer.normalize(value,Normalizer.Form.NFKC).toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]",""); }
    public static ReconciliationCandidate score(InvoiceView invoice,BankTransactionView transaction) {
        var reasons=new ArrayList<String>();int score=0;
        if(transaction.matched() || !invoice.currency().equals(transaction.currency())) return new ReconciliationCandidate(transaction,0,reasons);
        if(invoice.amount().compareTo(transaction.amount())==0) { score+=60;reasons.add("Exact amount and currency"); }
        if(normalize(invoice.reference()).equals(normalize(transaction.reference()))) { score+=30;reasons.add("Exact normalized invoice reference"); }
        if(normalize(invoice.vendorName()).equals(normalize(transaction.counterparty()))) { score+=10;reasons.add("Exact normalized vendor name"); }
        return new ReconciliationCandidate(transaction,score,reasons);
    }
}
