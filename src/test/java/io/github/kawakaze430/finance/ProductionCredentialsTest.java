package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.assertj.core.api.Assertions.*;
public class ProductionCredentialsTest {
 @Test void rejectsDemoPasswordOutsideDemoProfile() {
  var env=new MockEnvironment();env.setActiveProfiles("prod");
  env.setProperty("finance.users.analyst-password","demo-analyst-password");
  assertThatThrownBy(()->new SecurityConfiguration().users(env,new BCryptPasswordEncoder())).isInstanceOf(IllegalStateException.class);
 }
 @Test void missingProductionSecretsFailRatherThanUsingDemoDefaults() {
  var env=new MockEnvironment();env.setActiveProfiles("prod");
  assertThatThrownBy(()->new SecurityConfiguration().users(env,new BCryptPasswordEncoder())).isInstanceOf(IllegalStateException.class);
 }
}
