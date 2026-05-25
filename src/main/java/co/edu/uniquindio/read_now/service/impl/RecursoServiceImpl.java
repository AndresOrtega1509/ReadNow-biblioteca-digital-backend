package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.RecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.RecursoResponseDTO;
import co.edu.uniquindio.read_now.model.CategoriaRecurso;
import co.edu.uniquindio.read_now.model.HistoriaLectura;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.TipoRecurso;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.ICalificacionRepository;
import co.edu.uniquindio.read_now.repository.ICategoriaRecursoRepository;
import co.edu.uniquindio.read_now.repository.IHistoriaLecturaRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.ITipoRecursoRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IFirebaseStorageService;
import co.edu.uniquindio.read_now.service.IRecursoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecursoServiceImpl implements IRecursoService {

    /** Cliente reutilizable: HTTP/2 hacia Storage suele ir mejor con muchas peticiones Range de pdf.js. */
    private static final HttpClient FIREBASE_PROXY_HTTP = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(45))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final int PROXY_STREAM_BUFFER = 64 * 1024;
    private static final int MAX_RECOMENDADOS = 12;

    private final IRecursoRepository recursoRepository;
    private final ITipoRecursoRepository tipoRecursoRepository;
    private final ICategoriaRecursoRepository categoriaRecursoRepository;
    private final ICalificacionRepository calificacionRepository;
    private final IHistoriaLecturaRepository historiaLecturaRepository;
    private final IUsuarioRepository usuarioRepository;
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
    public List<RecursoResponseDTO> obtenerRecomendados(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<HistoriaLectura> historial = historiaLecturaRepository.findHistorialActivosByUsuario(usuario);
        if (historial.isEmpty()) {
            return List.of();
        }

        Set<Long> leidosIds = new HashSet<>();
        Map<Long, Integer> categoriaFreq = new HashMap<>();
        Map<Long, Integer> tipoFreq = new HashMap<>();
        Set<String> autoresLeidos = new HashSet<>();

        for (HistoriaLectura hl : historial) {
            Recurso leido = hl.getRecurso();
            leidosIds.add(leido.getRecursoId());
            if (leido.getCategoriaRecurso() != null) {
                Long catId = leido.getCategoriaRecurso().getCategoriaRecursoId();
                categoriaFreq.merge(catId, 1, Integer::sum);
            }
            if (leido.getTipoRecurso() != null) {
                Long tipoId = leido.getTipoRecurso().getTipoRecursoId();
                tipoFreq.merge(tipoId, 1, Integer::sum);
            }
            String autor = normalizarAutor(leido.getAutor());
            if (autor != null) {
                autoresLeidos.add(autor);
            }
        }

        List<Recurso> candidatos = recursoRepository.findByActivo("S");
        return candidatos.stream()
                .filter(r -> !leidosIds.contains(r.getRecursoId()))
                .filter(r -> puntuarRecomendacion(r, categoriaFreq, tipoFreq, autoresLeidos) > 0)
                .sorted(Comparator
                        .comparingInt((Recurso r) -> puntuarRecomendacion(r, categoriaFreq, tipoFreq, autoresLeidos))
                        .reversed()
                        .thenComparing(r -> calificacionRepository.findPromedioByRecursoId(r.getRecursoId()),
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(MAX_RECOMENDADOS)
                .map(this::toResponseDTO)
                .toList();
    }

    private static int puntuarRecomendacion(
            Recurso candidato,
            Map<Long, Integer> categoriaFreq,
            Map<Long, Integer> tipoFreq,
            Set<String> autoresLeidos) {
        int score = 0;
        if (candidato.getCategoriaRecurso() != null) {
            score += categoriaFreq.getOrDefault(
                    candidato.getCategoriaRecurso().getCategoriaRecursoId(), 0) * 10;
        }
        if (candidato.getTipoRecurso() != null) {
            score += tipoFreq.getOrDefault(
                    candidato.getTipoRecurso().getTipoRecursoId(), 0) * 8;
        }
        String autor = normalizarAutor(candidato.getAutor());
        if (autor != null && autoresLeidos.contains(autor)) {
            score += 15;
        }
        return score;
    }

    private static String normalizarAutor(String autor) {
        if (autor == null || autor.isBlank()) {
            return null;
        }
        return autor.trim().toLowerCase();
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

    @Override
    public void streamArchivoRecurso(Long recursoId, String rangeHeader, HttpServletResponse response)
            throws IOException {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        if (!"S".equals(recurso.getActivo())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Recurso no disponible");
            return;
        }
        String fileUrl = recurso.getUrlArchivo();
        if (fileUrl == null || fileUrl.isBlank()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "El recurso no tiene archivo");
            return;
        }

        response.setBufferSize(4 * 1024);

        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder(URI.create(fileUrl.trim()))
                .timeout(Duration.ofSeconds(300))
                .GET();
        if (rangeHeader != null && !rangeHeader.isBlank()) {
            reqBuilder.header("Range", rangeHeader.trim());
        }
        HttpRequest httpRequest = reqBuilder.build();

        final HttpResponse<InputStream> upstream;
        try {
            upstream = FIREBASE_PROXY_HTTP.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Interrupted");
            return;
        }

        int code = upstream.statusCode();
        response.setStatus(code);

        java.net.http.HttpHeaders uh = upstream.headers();
        uh.firstValue("Content-Type").ifPresent(response::setContentType);
        if (response.getContentType() == null || response.getContentType().isBlank()) {
            response.setContentType(inferirContentTypeArchivo(fileUrl));
        }

        copyClientHeader(uh, response, "Accept-Ranges");
        copyClientHeader(uh, response, "Content-Range");
        copyClientHeader(uh, response, "Content-Disposition");
        copyClientHeader(uh, response, "ETag");
        copyClientHeader(uh, response, "Cache-Control");

        uh.firstValue("Content-Length")
                .flatMap(cl -> {
                    try {
                        return Optional.of(Long.parseLong(cl.trim()));
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                })
                .ifPresent(response::setContentLengthLong);

        try (InputStream in = upstream.body(); OutputStream out = response.getOutputStream()) {
            byte[] buf = new byte[PROXY_STREAM_BUFFER];
            int n;
            boolean first = true;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
                if (first) {
                    out.flush();
                    response.flushBuffer();
                    first = false;
                }
            }
            out.flush();
        }
    }

    private static String inferirContentTypeArchivo(String fileUrl) {
        if (fileUrl == null) {
            return "application/octet-stream";
        }
        String path = fileUrl;
        int q = path.indexOf('?');
        if (q >= 0) {
            path = path.substring(0, q);
        }
        if (path.toLowerCase().endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }

    private static void copyClientHeader(java.net.http.HttpHeaders from, HttpServletResponse to, String name) {
        from.firstValue(name).ifPresent(v -> {
            if (!v.isBlank()) {
                to.setHeader(name, v);
            }
        });
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
