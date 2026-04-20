package co.edu.uniquindio.read_now.dto.response;

public record SuscripcionPlanResponseDTO(
        String codigoPlan,
        String nombre,
        String descripcion,
        double precioCop,
        int duracionMeses,
        boolean stripeConfigurado
) {}
