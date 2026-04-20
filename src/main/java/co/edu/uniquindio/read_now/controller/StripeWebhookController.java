package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.config.StripeProperties;
import co.edu.uniquindio.read_now.service.impl.SuscripcionStripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final StripeProperties stripeProperties;
    private final SuscripcionStripeWebhookService suscripcionStripeWebhookService;

    /**
     * Recibe eventos de Stripe (pago completado, renovación, etc.).
     * Configura en Dashboard → Webhooks la URL pública de este endpoint y el secreto whsec_ en STRIPE_WEBHOOK_SECRET.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {

        String secret = stripeProperties.getWebhookSecret();
        if (secret == null || secret.isBlank()) {
            log.warn("Webhook Stripe recibido pero app.stripe.webhook-secret está vacío.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("webhook no configurado");
        }
        if (sigHeader == null || sigHeader.isBlank()) {
            return ResponseEntity.badRequest().body("missing Stripe-Signature");
        }

        final Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, secret);
        } catch (SignatureVerificationException e) {
            log.warn("Webhook Stripe: firma inválida — {}", e.getMessage());
            return ResponseEntity.badRequest().body("invalid signature");
        }

        try {
            suscripcionStripeWebhookService.procesarEvento(event, payload);
        } catch (RuntimeException e) {
            log.error("Error procesando webhook Stripe", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
        return ResponseEntity.ok("ok");
    }
}
