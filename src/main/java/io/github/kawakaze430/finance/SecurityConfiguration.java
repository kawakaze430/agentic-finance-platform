package io.github.kawakaze430.finance;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfiguration {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService users(Environment env,PasswordEncoder encoder) {
        var manager = new InMemoryUserDetailsManager();
        for (String name : new String[]{"analyst","approver","admin"}) {
            String password = env.getRequiredProperty("finance.users."+name+"-password");
            if (!env.matchesProfiles("demo") && (password.startsWith("demo-") || password.length()<16))
                throw new IllegalStateException("Non-demo accounts require individual passwords of at least 16 characters");
            manager.createUser(User.withUsername(name).password(encoder.encode(password)).roles(name.toUpperCase(java.util.Locale.ROOT)).build());
        }
        return manager;
    }
    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.csrf(c->c.disable())
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .requestCache(c->c.disable())
            .httpBasic(b->b.authenticationEntryPoint((request,response,e)->{
                response.setStatus(401); response.setContentType("application/problem+json");
                response.getWriter().write("{\"status\":401,\"title\":\"Authentication required\"}");
            }))
            .headers(h->h.contentSecurityPolicy(c->c.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; connect-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none'")))
            .authorizeHttpRequests(a->a
                .requestMatchers("/","/index.html","/app.js","/styles.css","/favicon.svg","/actuator/health","/error").permitAll()
                .requestMatchers("/actuator/**","/api/outbox/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,"/api/invoices/*/approve","/api/invoices/*/reject","/api/reconciliation/matches").hasAnyRole("APPROVER","ADMIN")
                .requestMatchers(HttpMethod.POST,"/api/ledger/journals").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,"/api/**").hasAnyRole("ANALYST","ADMIN")
                .anyRequest().authenticated()).build();
    }
}
