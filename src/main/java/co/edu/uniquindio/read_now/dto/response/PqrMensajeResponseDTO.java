package co.edu.uniquindio.read_now.dto.response;

import java.time.LocalDateTime;

public record PqrMensajeResponseDTO(
        Long mensajeId,
        String autorNombre,
        boolean esAdmin,
        String contenido,
        LocalDateTime fechaCreacion
) {}
