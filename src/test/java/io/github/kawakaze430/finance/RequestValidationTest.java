package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.http.MediaType;
public class RequestValidationTest extends IntegrationSupport {
 @Test void rejectsMissingInvoiceFields()throws Exception{mvc.perform(post("/api/invoices").with(httpBasic("analyst","demo-analyst-password")).header("Idempotency-Key",key()).contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));}
 @Test void rejectsUnknownFields()throws Exception{mvc.perform(post("/api/vendors").with(httpBasic("analyst","demo-analyst-password")).header("Idempotency-Key",key()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Acme\",\"email\":\"a@acme.example\",\"currency\":\"INR\",\"admin\":true}" )).andExpect(status().isBadRequest());}
 @Test void requiresIdempotencyHeader()throws Exception{mvc.perform(post("/api/vendors").with(httpBasic("analyst","demo-analyst-password")).contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isBadRequest());}
}
