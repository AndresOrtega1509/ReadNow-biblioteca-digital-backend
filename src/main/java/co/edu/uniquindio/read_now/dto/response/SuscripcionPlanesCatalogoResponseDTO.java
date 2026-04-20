package co.edu.uniquindio.read_now.dto.response;

import java.util.List;

public record SuscripcionPlanesCatalogoResponseDTO(
        List<SuscripcionPlanResponseDTO> planesPago,
        boolean puedeActivarPruebaGratuita,
        int diasPruebaGratuita,
        /** True si ya hay cliente Stripe (puede abrir portal para renovar/cancelar). */
        boolean puedeGestionarEnStripe
) {}
