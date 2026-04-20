package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.config.StripeProperties;
import co.edu.uniquindio.read_now.dto.request.StripeCheckoutSessionRequestDTO;
import co.edu.uniquindio.read_now.dto.response.StripeCheckoutSessionResponseDTO;
import co.edu.uniquindio.read_now.dto.response.StripePublishableKeyResponseDTO;
import co.edu.uniquindio.read_now.service.impl.StripePaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/payments/stripe")
@RequiredArgsConstructor
public class StripePaymentController {

    private final StripePaymentService stripePaymentService;
    private final StripeProperties stripeProperties;

    /** Clave publicable para el front (Stripe.js / redirección a Checkout). */
    @GetMapping("/config")
    public ResponseEntity<StripePublishableKeyResponseDTO> publishableKey() {
        String pk = stripeProperties.getPublishableKey();
        if (pk == null || pk.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new StripePublishableKeyResponseDTO(""));
        }
        return ResponseEntity.ok(new StripePublishableKeyResponseDTO(pk));
    }

    /** Crea sesión Checkout en modo suscripción; el front redirige a la URL devuelta. */
    @PostMapping("/checkout-session")
    public ResponseEntity<StripeCheckoutSessionResponseDTO> crearCheckout(
            @Valid @RequestBody StripeCheckoutSessionRequestDTO body,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(stripePaymentService.crearCheckoutConPriceId(principal.getName(), body.priceId()));
    }
}
