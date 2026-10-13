package io.github.kawakaze430.finance;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import java.net.*;import java.net.http.HttpClient;import java.nio.charset.StandardCharsets;
import java.util.*;
public class OllamaProtocolTest extends IntegrationSupport {
 @Test void executesNativeToolCallsAndParsesFinalRecommendation() throws Exception {
    var invoice=invoice("12");var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);var requests=new java.util.concurrent.atomic.AtomicInteger();
    server.createContext("/api/chat",exchange->{
        String request=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
        assertThat(request).contains("get_invoice").contains("search_policy");
        int n=requests.getAndIncrement();String reply;
        if(n==0) reply=json.writeValueAsString(Map.of("message",Map.of("role","assistant","content","","tool_calls",List.of(
            Map.of("function",Map.of("name","get_invoice","arguments",Map.of("invoice_id",invoice.id().toString()))),
            Map.of("function",Map.of("name","search_policy","arguments",Map.of("query","approval")))))));
        else reply=json.writeValueAsString(Map.of("message",Map.of("role","assistant","content",json.writeValueAsString(risk.assess(invoice)))));
        byte[] bytes=reply.getBytes(StandardCharsets.UTF_8);exchange.getResponseHeaders().set("Content-Type","application/json");exchange.sendResponseHeaders(200,bytes.length);exchange.getResponseBody().write(bytes);exchange.close();
    });server.start();
    try {
        var brain=new OllamaBrain(json,HttpClient.newHttpClient(),"http://127.0.0.1:"+server.getAddress().getPort(),"test-model");
        var runner=new AgentOrchestrator(invoices,risk,policies,tools,brain,runs,json,metrics,"ollama",6);
        var result=runner.run(new Requests.Agent(invoice.id(),"Review"),"analyst");
        assertThat(result.status()).isEqualTo(AgentStatus.COMPLETED);assertThat(result.mode()).isEqualTo("ollama");assertThat(result.trace()).hasSize(2);assertThat(requests.get()).isEqualTo(2);
    } finally {server.stop(0);}
 }
 @Test void upstreamFailureDoesNotBecomeASuccessfulRecommendation() throws Exception {
    var invoice=invoice("12");var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
    server.createContext("/api/chat",exchange->{exchange.sendResponseHeaders(503,-1);exchange.close();});server.start();
    try {var brain=new OllamaBrain(json,HttpClient.newHttpClient(),"http://127.0.0.1:"+server.getAddress().getPort(),"test-model");var runner=new AgentOrchestrator(invoices,risk,policies,tools,brain,runs,json,metrics,"ollama",6);var result=runner.run(new Requests.Agent(invoice.id(),"Review"),"analyst");assertThat(result.status()).isEqualTo(AgentStatus.FAILED);assertThat(result.errorCode()).isEqualTo("MODEL_UNAVAILABLE");assertThat(result.recommendation()).isNull();}
    finally {server.stop(0);}
 }
}
