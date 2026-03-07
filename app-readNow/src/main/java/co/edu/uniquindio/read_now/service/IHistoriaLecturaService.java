package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.response.HistoriaLecturaResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;

import java.util.List;

public interface IHistoriaLecturaService {

    MensajeResponseDTO registrarLectura(Long recursoId, Long usuarioId);

    List<HistoriaLecturaResponseDTO> obtenerHistorial(Long usuarioId);
}
