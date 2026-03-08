package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.RecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.RecursoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRecursoService {

    RecursoResponseDTO crearRecurso(RecursoRequestDTO request, MultipartFile archivo, MultipartFile portada);

    RecursoResponseDTO actualizarRecurso(Long recursoId, RecursoRequestDTO request, MultipartFile archivo, MultipartFile portada);

    void eliminarRecurso(Long recursoId);

    RecursoResponseDTO obtenerRecurso(Long recursoId);

    List<RecursoResponseDTO> listarRecursos();

    List<RecursoResponseDTO> buscarRecursos(String query);

    List<RecursoResponseDTO> obtenerMejorCalificados();

    List<RecursoResponseDTO> listarPorTipo(Long tipoRecursoId);

    List<RecursoResponseDTO> listarPorCategoria(Long categoriaId);
}
