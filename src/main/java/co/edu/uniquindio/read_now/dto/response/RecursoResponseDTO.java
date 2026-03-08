package co.edu.uniquindio.read_now.dto.response;

import java.time.LocalDate;

public record RecursoResponseDTO(
        Long recursoId,
        String nombre,
        String autor,
        String descripcion,
        String idioma,
        String urlArchivo,
        String urlPortada,
        LocalDate fechaPublicacion,
        String tipoRecurso,
        String categoriaRecurso,
        Double calificacionPromedio,
        Long totalCalificaciones
) {}
