package io.github.kawakaze430.finance;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class JsonConfiguration {
    @Bean Jackson2ObjectMapperBuilderCustomizer exactDecimals() {
        return builder->builder.serializerByType(BigDecimal.class,new ToStringSerializer());
    }
}
