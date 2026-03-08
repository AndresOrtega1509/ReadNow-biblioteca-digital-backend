package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.TipoRecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.TipoRecursoResponseDTO;

import java.util.List;

public interface ITipoRecursoService {

    List<TipoRecursoResponseDTO> listarTodos();

    TipoRecursoResponseDTO crear(TipoRecursoRequestDTO request);

    TipoRecursoResponseDTO actualizar(Long id, TipoRecursoRequestDTO request);

    void eliminar(Long id);
}
