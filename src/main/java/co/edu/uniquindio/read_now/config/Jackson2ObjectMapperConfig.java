package co.edu.uniquindio.read_now.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4 auto-configura {@code JsonMapper} (Jackson 3), no {@link ObjectMapper} (Jackson 2).
 * El webhook de Stripe parsea JSON con Jackson 2; este bean permite inyectar {@link ObjectMapper}.
 */
@Configuration
public class Jackson2ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
