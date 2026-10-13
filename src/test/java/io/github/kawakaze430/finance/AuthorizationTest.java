package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.http.MediaType;
public class AuthorizationTest extends IntegrationSupport {
 @Test void analystCannotApprove()throws Exception{var i=invoice("100");mvc.perform(post("/api/invoices/"+i.id()+"/approve").with(httpBasic("analyst","demo-analyst-password")).header("Idempotency-Key",key()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(decision()))).andExpect(status().isForbidden());assertThat(invoices.get(i.id()).status()).isEqualTo(InvoiceStatus.RECEIVED);}
 @Test void approverCannotPostJournals()throws Exception{mvc.perform(post("/api/ledger/journals").with(httpBasic("approver","demo-approver-password")).contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isForbidden());}
 @Test void analystCannotReadAdministrativeOutbox()throws Exception{mvc.perform(get("/api/outbox").with(httpBasic("analyst","demo-analyst-password"))).andExpect(status().isForbidden());}
}
