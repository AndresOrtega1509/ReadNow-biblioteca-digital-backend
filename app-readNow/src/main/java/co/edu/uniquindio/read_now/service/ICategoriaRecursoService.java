package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.CategoriaRecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.CategoriaRecursoResponseDTO;

import java.util.List;

public interface ICategoriaRecursoService {

    List<CategoriaRecursoResponseDTO> listarTodas();

    CategoriaRecursoResponseDTO crear(CategoriaRecursoRequestDTO request);

    CategoriaRecursoResponseDTO actualizar(Long id, CategoriaRecursoRequestDTO request);

    void eliminar(Long id);
}
