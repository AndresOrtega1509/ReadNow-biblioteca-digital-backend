package co.edu.uniquindio.read_now.dto.response;

public record SesionConfigResponseDTO(
        long inactividadLecturaMs,
        long inactividadCatalogoMs,
        long countdownMs
) {}
