package co.edu.uniquindio.read_now.config;

import com.stripe.StripeClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StripeClientProvider {

    private final StripeProperties stripeProperties;
    private StripeClient stripeClient;

    @PostConstruct
    void init() {
        String k = stripeProperties.getSecretKey();
        if (k != null && !k.isBlank()) {
            stripeClient = new StripeClient(k);
            log.info("StripeClient listo.");
        } else {
            log.warn("StripeClient no disponible: falta app.stripe.secret-key.");
        }
    }

    public Optional<StripeClient> client() {
        return Optional.ofNullable(stripeClient);
    }
}
