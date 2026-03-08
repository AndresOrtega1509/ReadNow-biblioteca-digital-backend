package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.RecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.RecursoResponseDTO;
import co.edu.uniquindio.read_now.model.CategoriaRecurso;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.TipoRecurso;
import co.edu.uniquindio.read_now.repository.ICalificacionRepository;
import co.edu.uniquindio.read_now.repository.ICategoriaRecursoRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.ITipoRecursoRepository;
import co.edu.uniquindio.read_now.service.IFirebaseStorageService;
import co.edu.uniquindio.read_now.service.IRecursoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecursoServiceImpl implements IRecursoService {

    private final IRecursoRepository recursoRepository;
    private final ITipoRecursoRepository tipoRecursoRepository;
    private final ICategoriaRecursoRepository categoriaRecursoRepository;
    private final ICalificacionRepository calificacionRepository;
    private final IFirebaseStorageService firebaseStorageService;

    @Override
    @Transactional
    public RecursoResponseDTO crearRecurso(RecursoRequestDTO request, MultipartFile archivo, MultipartFile portada) {
        TipoRecurso tipoRecurso = tipoRecursoRepository.findById(request.tipoRecursoId())
                .orElseThrow(() -> new RuntimeException("Tipo de recurso no encontrado"));

        CategoriaRecurso categoriaRecurso = null;
        if (request.categoriaRecursoId() != null) {
            categoriaRecurso = categoriaRecursoRepository.findById(request.categoriaRecursoId())
                    .orElseThrow(() -> new RuntimeException("Categoría de recurso no encontrada"));
        }

        String urlPortadaInicial = request.urlPortada() != null && !request.urlPortada().isBlank() ? request.urlPortada() : null;
        if (portada != null && !portada.isEmpty()) {
            urlPortadaInicial = null;
        }

        Recurso recurso = Recurso.builder()
                .nombre(request.nombre())
                .autor(request.autor())
                .descripcion(request.descripcion())
                .idioma(request.idioma())
                .urlPortada(urlPortadaInicial)
                .fechaPublicacion(request.fechaPublicacion() != null ? request.fechaPublicacion() : LocalDate.now())
                .activo("S")
                .tipoRecurso(tipoRecurso)
                .categoriaRecurso(categoriaRecurso)
                .build();

        recurso = recursoRepository.save(recurso);

        if (archivo != null && !archivo.isEmpty()) {
            String url = firebaseStorageService.subirArchivo(archivo, recurso.getRecursoId());
            recurso.setUrlArchivo(url);
        }
        if (portada != null && !portada.isEmpty()) {
            String urlPortada = firebaseStorageService.subirPortada(portada, recurso.getRecursoId());
            recurso.setUrlPortada(urlPortada);
        }
        if (archivo != null && !archivo.isEmpty() || portada != null && !portada.isEmpty()) {
            recurso = recursoRepository.save(recurso);
        }

        log.info("Recurso creado: {} (ID: {})", recurso.getNombre(), recurso.getRecursoId());
        return toResponseDTO(recurso);
    }

    @Override
    @Transactional
    public RecursoResponseDTO actualizarRecurso(Long recursoId, RecursoRequestDTO request, MultipartFile archivo, MultipartFile portada) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        TipoRecurso tipoRecurso = tipoRecursoRepository.findById(request.tipoRecursoId())
                .orElseThrow(() -> new RuntimeException("Tipo de recurso no encontrado"));

        CategoriaRecurso categoriaRecurso = null;
        if (request.categoriaRecursoId() != null) {
            categoriaRecurso = categoriaRecursoRepository.findById(request.categoriaRecursoId())
                    .orElseThrow(() -> new RuntimeException("Categoría de recurso no encontrada"));
        }

        recurso.setNombre(request.nombre());
        recurso.setAutor(request.autor());
        recurso.setDescripcion(request.descripcion());
        recurso.setIdioma(request.idioma());
        recurso.setFechaPublicacion(request.fechaPublicacion() != null ? request.fechaPublicacion() : recurso.getFechaPublicacion());
        recurso.setTipoRecurso(tipoRecurso);
        recurso.setCategoriaRecurso(categoriaRecurso);

        if (portada != null && !portada.isEmpty()) {
            firebaseStorageService.eliminarPortada(recursoId);
            String urlPortada = firebaseStorageService.subirPortada(portada, recursoId);
            recurso.setUrlPortada(urlPortada);
        } else if (request.urlPortada() != null && !request.urlPortada().isBlank()) {
            recurso.setUrlPortada(request.urlPortada());
        }

        if (archivo != null && !archivo.isEmpty()) {
            String url = firebaseStorageService.subirArchivo(archivo, recursoId);
            recurso.setUrlArchivo(url);
        }

        recurso = recursoRepository.save(recurso);
        log.info("Recurso actualizado: {} (ID: {})", recurso.getNombre(), recursoId);
        return toResponseDTO(recurso);
    }

    @Override
    @Transactional
    public void eliminarRecurso(Long recursoId) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        recurso.setActivo("N");
        recursoRepository.save(recurso);

        firebaseStorageService.eliminarArchivo(recursoId);
        log.info("Recurso eliminado (soft delete): ID {}", recursoId);
    }

    @Override
    public RecursoResponseDTO obtenerRecurso(Long recursoId) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        return toResponseDTO(recurso);
    }

    @Override
    public List<RecursoResponseDTO> listarRecursos() {
        return recursoRepository.findByActivo("S").stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<RecursoResponseDTO> buscarRecursos(String query) {
        return recursoRepository.findByActivoAndNombreContainingIgnoreCase("S", query).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<RecursoResponseDTO> obtenerMejorCalificados() {
        return recursoRepository.findMejorCalificados().stream()
                .limit(10)
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<RecursoResponseDTO> listarPorTipo(Long tipoRecursoId) {
        return recursoRepository.findByTipoRecursoTipoRecursoIdAndActivo(tipoRecursoId, "S").stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<RecursoResponseDTO> listarPorCategoria(Long categoriaId) {
        return recursoRepository.findByCategoriaRecursoCategoriaRecursoIdAndActivo(categoriaId, "S").stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private RecursoResponseDTO toResponseDTO(Recurso recurso) {
        Double promedio = calificacionRepository.findPromedioByRecursoId(recurso.getRecursoId());
        long totalCalificaciones = calificacionRepository.countByRecursoRecursoId(recurso.getRecursoId());

        return new RecursoResponseDTO(
                recurso.getRecursoId(),
                recurso.getNombre(),
                recurso.getAutor(),
                recurso.getDescripcion(),
                recurso.getIdioma(),
                recurso.getUrlArchivo(),
                recurso.getUrlPortada(),
                recurso.getFechaPublicacion(),
                recurso.getTipoRecurso() != null ? recurso.getTipoRecurso().getNombre() : null,
                recurso.getCategoriaRecurso() != null ? recurso.getCategoriaRecurso().getNombre() : null,
                promedio,
                totalCalificaciones
        );
    }
}
