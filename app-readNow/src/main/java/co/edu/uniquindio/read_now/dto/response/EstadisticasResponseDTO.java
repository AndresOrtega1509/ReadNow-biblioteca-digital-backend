package co.edu.uniquindio.read_now.dto.response;

import java.util.List;

public record EstadisticasResponseDTO(
        long totalUsuarios,
        long totalRecursos,
        long totalLecturas,
        long totalCalificaciones,
        long totalResenias,
        long totalFavoritos,
        long suscripcionesActivas,
        long suscripcionesVencidas,
        List<SuscripcionPorPlanDTO> suscripcionesPorPlan,
        List<RecursoResponseDTO> recursosMasLeidos,
        List<RecursoResponseDTO> recursosMejorCalificados,
        List<RecursoPorTipoDTO> recursosPorTipo
) {
    public record RecursoPorTipoDTO(
            String tipoRecurso,
            long cantidad
    ) {}

    public record SuscripcionPorPlanDTO(
            String nombrePlan,
            long activas,
            long vencidas
    ) {}
}
