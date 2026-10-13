package io.github.kawakaze430.finance;

import java.net.http.HttpClient;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
@Configuration
public class AgentConfiguration {
    @Bean AgentBrain agentBrain(ObjectMapper json,@Value("${finance.agent.mode}") String mode,@Value("${finance.agent.ollama-base-url}") String baseUrl,@Value("${finance.agent.model}") String model) {
        return switch(mode) {
            case "demo" -> new DemoBrain(json);
            case "ollama" -> new OllamaBrain(json,HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build(),baseUrl,model);
            default -> throw new IllegalArgumentException("AGENT_MODE must be demo or ollama");
        };
    }
}
