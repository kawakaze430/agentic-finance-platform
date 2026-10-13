package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
public class DecimalResponseTest extends IntegrationSupport {
 @Test void moneyIsSerializedAsAnExactString() throws Exception {
  var i=invoice("99999999999999.99");
  mvc.perform(get("/api/invoices/"+i.id()).with(httpBasic("analyst","demo-analyst-password")))
    .andExpect(status().isOk()).andExpect(jsonPath("$.amount").isString()).andExpect(jsonPath("$.amount").value("99999999999999.99"));
 }
}
