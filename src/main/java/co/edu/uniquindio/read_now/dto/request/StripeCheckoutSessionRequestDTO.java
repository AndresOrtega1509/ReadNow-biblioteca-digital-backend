package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StripeCheckoutSessionRequestDTO(
        @NotBlank(message = "El priceId de Stripe es obligatorio (ej. price_xxx del Dashboard)")
        String priceId
) {}
