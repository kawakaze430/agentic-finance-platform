package io.github.kawakaze430.finance;

import com.fasterxml.jackson.databind.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import org.springframework.http.HttpStatus;
public class OllamaBrain implements AgentBrain {
    final ObjectMapper json;final HttpClient client;final URI endpoint;final String model;
    public OllamaBrain(ObjectMapper json,HttpClient client,String baseUrl,String model) {
        URI uri=URI.create(baseUrl);
        if(!Set.of("http","https").contains(uri.getScheme()) || uri.getHost()==null || uri.getUserInfo()!=null || uri.getQuery()!=null || uri.getFragment()!=null) throw new IllegalArgumentException("Ollama base URL must be an HTTP(S) host");
        this.json=json;this.client=client;this.endpoint=URI.create(baseUrl.replaceAll("/+$","")+"/api/chat");this.model=model;
    }
    @Override public BrainReply respond(InvoiceView invoice,Recommendation baseline,List<Map<String,Object>> messages,List<Map<String,Object>> tools,int iteration) {
        try {
            String body=json.writeValueAsString(Map.of("model",model,"messages",messages,"tools",tools,"stream",false,"options",Map.of("temperature",0,"num_predict",1024)));
            var request=HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(10)).header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
            var response=client.send(request,HttpResponse.BodyHandlers.ofString());
            if(response.statusCode()!=200) throw new DomainException(HttpStatus.BAD_GATEWAY,"MODEL_UNAVAILABLE","Ollama did not return a successful response");
            if(response.body().length()>100000) throw DomainException.invalid("Model response exceeded the size limit");
            JsonNode message=json.readTree(response.body()).path("message");
            var calls=new ArrayList<ToolCall>();
            if(message.has("tool_calls")) {
                if(!message.path("tool_calls").isArray()) throw DomainException.invalid("Invalid model tool response");
                for(JsonNode call:message.path("tool_calls")) {
                    var fn=call.path("function");var arguments=fn.path("arguments");
                    if(arguments.isTextual()) arguments=json.readTree(arguments.asText());
                    calls.add(new ToolCall(fn.path("name").asText(),arguments));
                }
            }
            if(!calls.isEmpty()) return new BrainReply(List.copyOf(calls),null);
            return new BrainReply(List.of(),json.readValue(message.path("content").asText(),Recommendation.class));
        } catch(java.net.http.HttpTimeoutException e) {
            throw new DomainException(HttpStatus.GATEWAY_TIMEOUT,"MODEL_TIMEOUT","Ollama request timed out");
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();throw new DomainException(HttpStatus.SERVICE_UNAVAILABLE,"MODEL_INTERRUPTED","Model request interrupted");
        } catch(java.io.IOException e) {
            throw new DomainException(HttpStatus.BAD_GATEWAY,"MODEL_PROTOCOL_ERROR","Ollama response was unavailable or did not follow the expected schema");
        }
    }
}
