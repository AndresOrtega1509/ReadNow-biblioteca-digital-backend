package co.edu.uniquindio.read_now.dto.response;

import java.time.LocalDateTime;

public record ReseniaResponseDTO(
        Long reseniaId,
        Long recursoId,
        String nombreUsuario,
        String descripcion,
        LocalDateTime fechaCreacion
) {}
