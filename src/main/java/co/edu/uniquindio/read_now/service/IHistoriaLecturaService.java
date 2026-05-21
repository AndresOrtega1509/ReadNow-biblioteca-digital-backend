package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.LecturaAnotacionesRequestDTO;
import co.edu.uniquindio.read_now.dto.request.LecturaProgresoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.HistoriaLecturaResponseDTO;
import co.edu.uniquindio.read_now.dto.response.LecturaAnotacionesResponseDTO;
import co.edu.uniquindio.read_now.dto.response.LecturaProgresoResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;

import java.util.List;

public interface IHistoriaLecturaService {

    MensajeResponseDTO registrarLectura(Long recursoId, Long usuarioId);

    List<HistoriaLecturaResponseDTO> obtenerHistorial(Long usuarioId);

    /** Recursos con progreso guardado, más recientes primero (estilo “continuar viendo”). */
    List<HistoriaLecturaResponseDTO> listarContinuarLeyendo(Long usuarioId);

    MensajeResponseDTO actualizarProgreso(Long recursoId, Long usuarioId, LecturaProgresoRequestDTO request);

    LecturaProgresoResponseDTO obtenerProgreso(Long recursoId, Long usuarioId);

    LecturaAnotacionesResponseDTO obtenerAnotaciones(Long recursoId, Long usuarioId);

    MensajeResponseDTO guardarAnotaciones(Long recursoId, Long usuarioId, LecturaAnotacionesRequestDTO request);
}
