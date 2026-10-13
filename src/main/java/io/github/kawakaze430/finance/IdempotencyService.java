package io.github.kawakaze430.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.util.HexFormat;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class IdempotencyService {
    final IdempotencyRecordRepository repository; final ObjectMapper json; final Clock clock;
    public IdempotencyService(IdempotencyRecordRepository repository,ObjectMapper json,Clock clock) { this.repository=repository;this.json=json;this.clock=clock; }
    @Transactional
    public <T> T execute(String actor,String key,String command,Object request,Class<T> type,Supplier<T> operation) {
        if(key==null || !key.matches("[A-Za-z0-9._-]{8,80}")) throw DomainException.invalid("Idempotency-Key must contain 8 to 80 letters, numbers, dots, dashes, or underscores");
        try {
            String fingerprint=HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest((command+"\n"+json.writeValueAsString(request)).getBytes(StandardCharsets.UTF_8)));
            var existing=repository.findByActorAndRequestKey(actor,key);
            if(existing.isPresent()) {
                var record=existing.get();
                if(!record.fingerprint.equals(fingerprint)) throw DomainException.conflict("Idempotency key was already used for a different command");
                if(record.response==null) throw DomainException.conflict("Command is already in progress");
                return json.readValue(record.response,type);
            }
            var record=new IdempotencyRecord();record.actor=actor;record.requestKey=key;record.fingerprint=fingerprint;record.createdAt=clock.instant();
            repository.saveAndFlush(record);
            T result=operation.get(); record.response=json.writeValueAsString(result); repository.save(record); return result;
        } catch (com.fasterxml.jackson.core.JsonProcessingException | java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not serialize an idempotent command",e);
        }
    }
}
