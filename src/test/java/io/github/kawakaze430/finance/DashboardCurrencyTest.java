package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;import java.time.LocalDate;
public class DashboardCurrencyTest extends IntegrationSupport {
 @org.springframework.beans.factory.annotation.Autowired DashboardService dashboard;
 @Test void totalsDoNotAddDifferentCurrencies(){var before=dashboard.summary("INR").outstandingAmount();var v=vendor("USD");invoices.create(new Requests.Invoice(v.id(),key(),new BigDecimal("100"),"USD",LocalDate.now(clock)),"fixture");assertThat(dashboard.summary("INR").outstandingAmount()).isEqualByComparingTo(before);assertThat(dashboard.summary("USD").outstandingAmount()).isGreaterThanOrEqualTo(new BigDecimal("100"));}
}
