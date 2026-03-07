package co.edu.uniquindio.read_now.dto.response;

public record FavoritoResponseDTO(
        Long favoritoId,
        Long recursoId,
        String nombreRecurso,
        String autorRecurso,
        String urlArchivo,
        String urlPortada
) {}
