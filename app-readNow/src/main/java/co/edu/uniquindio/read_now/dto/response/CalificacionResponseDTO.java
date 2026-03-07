package co.edu.uniquindio.read_now.dto.response;

public record CalificacionResponseDTO(
        Long calificacionId,
        Long recursoId,
        Long usuarioId,
        String nombreUsuario,
        int valor
) {}
