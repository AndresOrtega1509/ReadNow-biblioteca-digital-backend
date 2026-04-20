package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SuscripcionCheckoutCodigoRequestDTO(
        @NotBlank(message = "codigoPlan es obligatorio (MENSUAL, SEMESTRAL o ANUAL)")
        String codigoPlan
) {}
