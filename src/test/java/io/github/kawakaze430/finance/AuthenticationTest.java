package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.http.MediaType;
public class AuthenticationTest extends IntegrationSupport {
 @Test void rejectsUnauthenticatedApiRequests()throws Exception{mvc.perform(get("/api/dashboard")).andExpect(status().isUnauthorized());}
 @Test void permitsHealthWithoutCredentials()throws Exception{mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));}
 @Test void acceptsAnalystCredentials()throws Exception{mvc.perform(get("/api/dashboard").with(httpBasic("analyst","demo-analyst-password"))).andExpect(status().isOk());}
 @Test void rejectsIncorrectPassword()throws Exception{mvc.perform(get("/api/dashboard").with(httpBasic("analyst","wrong"))).andExpect(status().isUnauthorized());}
}
