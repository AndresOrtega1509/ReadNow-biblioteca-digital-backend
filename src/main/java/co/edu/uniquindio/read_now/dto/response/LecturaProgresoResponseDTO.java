package co.edu.uniquindio.read_now.dto.response;

/**
 * Progreso guardado para un recurso (respuesta compacta).
 */
public record LecturaProgresoResponseDTO(
        Integer ultimaPagina,
        Integer totalPaginas,
        Integer progresoPorcentaje
) {}
