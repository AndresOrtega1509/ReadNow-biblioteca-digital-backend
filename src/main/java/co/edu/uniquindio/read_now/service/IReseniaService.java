package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.ReseniaRequestDTO;
import co.edu.uniquindio.read_now.dto.response.ReseniaResponseDTO;

import java.util.List;

public interface IReseniaService {

    ReseniaResponseDTO crearResenia(ReseniaRequestDTO request, Long usuarioId);

    List<ReseniaResponseDTO> obtenerReseniasPorRecurso(Long recursoId);

    /** Solo administradores (validado en el controlador). */
    void eliminarReseniaComoAdmin(Long reseniaId);
}
