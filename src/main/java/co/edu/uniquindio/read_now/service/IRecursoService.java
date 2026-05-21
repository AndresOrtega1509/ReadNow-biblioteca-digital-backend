package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.RecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.RecursoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface IRecursoService {

    RecursoResponseDTO crearRecurso(RecursoRequestDTO request, MultipartFile archivo, MultipartFile portada);

    RecursoResponseDTO actualizarRecurso(Long recursoId, RecursoRequestDTO request, MultipartFile archivo, MultipartFile portada);

    void eliminarRecurso(Long recursoId);

    RecursoResponseDTO obtenerRecurso(Long recursoId);

    List<RecursoResponseDTO> listarRecursos();

    List<RecursoResponseDTO> buscarRecursos(String query);

    List<RecursoResponseDTO> obtenerMejorCalificados();

    /** Recursos sugeridos según el historial de lectura del usuario (estilo Netflix). */
    List<RecursoResponseDTO> obtenerRecomendados(Long usuarioId);

    List<RecursoResponseDTO> listarPorTipo(Long tipoRecursoId);

    List<RecursoResponseDTO> listarPorCategoria(Long categoriaId);

    /**
     * Transmite el archivo del recurso (p. ej. PDF en Firebase) al cliente.
     * Sirve como proxy para que pdf.js pueda usar Range y leer el cuerpo con CORS desde el front
     * (las respuestas parciales de Storage suelen no exponer ACAO al origen del SPA).
     */
    void streamArchivoRecurso(Long recursoId, String rangeHeader, HttpServletResponse response) throws IOException;
}
