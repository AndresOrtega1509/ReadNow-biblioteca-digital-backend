package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.CalificacionRequestDTO;
import co.edu.uniquindio.read_now.dto.response.CalificacionResponseDTO;

import java.util.List;

public interface ICalificacionService {

    CalificacionResponseDTO calificar(CalificacionRequestDTO request, Long usuarioId);

    Double obtenerPromedioCalificacion(Long recursoId);

    /** Valor (1-5) de la calificación del usuario para el recurso, o 0 si no ha calificado. */
    int obtenerMiCalificacion(Long recursoId, Long usuarioId);

    List<CalificacionResponseDTO> obtenerCalificacionesPorRecurso(Long recursoId);
}
