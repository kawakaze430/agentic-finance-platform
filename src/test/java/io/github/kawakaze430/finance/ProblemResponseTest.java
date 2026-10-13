package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.http.MediaType;
public class ProblemResponseTest extends IntegrationSupport {
 @Test void missingInvoiceReturnsSafeProblemWithRequestId()throws Exception{mvc.perform(get("/api/invoices/"+key()).with(httpBasic("analyst","demo-analyst-password")).header("X-Request-ID","safe-id")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("NOT_FOUND")).andExpect(jsonPath("$.requestId").value("safe-id" )).andExpect(header().string("X-Request-ID","safe-id"));}
 @Test void invalidIdsReturnBadRequest()throws Exception{mvc.perform(get("/api/invoices/not-a-uuid").with(httpBasic("analyst","demo-analyst-password"))).andExpect(status().isBadRequest());}
}
