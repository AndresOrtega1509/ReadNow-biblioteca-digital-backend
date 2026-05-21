package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.LecturaAnotacionesRequestDTO;
import co.edu.uniquindio.read_now.dto.request.LecturaProgresoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.HistoriaLecturaResponseDTO;
import co.edu.uniquindio.read_now.dto.response.LecturaAnotacionesResponseDTO;
import co.edu.uniquindio.read_now.dto.response.LecturaProgresoResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.model.HistoriaLectura;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IHistoriaLecturaRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IHistoriaLecturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HistoriaLecturaServiceImpl implements IHistoriaLecturaService {

    private static final int MAX_CONTINUAR = 12;

    private final IHistoriaLecturaRepository historiaLecturaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IRecursoRepository recursoRepository;

    @Override
    @Transactional
    public MensajeResponseDTO registrarLectura(Long recursoId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        List<HistoriaLectura> existentes = historiaLecturaRepository
                .findAllByUsuarioAndRecursoOrderByFechaLecturaDesc(usuario, recurso);

        if (!existentes.isEmpty()) {
            HistoriaLectura historia = existentes.size() == 1
                    ? existentes.get(0)
                    : obtenerOConsolidarHistoria(usuario, recurso);
            historia.setFechaLectura(LocalDateTime.now());
            historiaLecturaRepository.save(historia);
        }
        // No crear fila nueva aquí: el historial se crea al guardar progreso real (visor PDF / lectura).

        return new MensajeResponseDTO(true, "Lectura registrada exitosamente");
    }

    @Override
    @Transactional
    public MensajeResponseDTO actualizarProgreso(Long recursoId, Long usuarioId, LecturaProgresoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        int ultima = request.ultimaPagina();
        Integer totalReq = request.totalPaginas();
        if (totalReq != null && ultima > totalReq) {
            return new MensajeResponseDTO(false, "La página actual no puede ser mayor que el total de páginas.");
        }

        HistoriaLectura hl = obtenerOConsolidarHistoria(usuario, recurso);

        hl.setUltimaPagina(ultima);
        if (totalReq != null) {
            hl.setTotalPaginas(totalReq);
        }
        hl.setFechaLectura(LocalDateTime.now());
        historiaLecturaRepository.save(hl);
        return new MensajeResponseDTO(true, "Progreso guardado");
    }

    @Override
    public LecturaProgresoResponseDTO obtenerProgreso(Long recursoId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        List<HistoriaLectura> filas = historiaLecturaRepository
                .findAllByUsuarioAndRecursoOrderByFechaLecturaDesc(usuario, recurso);
        if (filas.isEmpty()) {
            return new LecturaProgresoResponseDTO(null, null, null);
        }
        HistoriaLectura hl = filas.size() == 1 ? filas.get(0) : obtenerOConsolidarHistoria(usuario, recurso);
        return toProgresoDto(hl);
    }

    @Override
    public List<HistoriaLecturaResponseDTO> obtenerHistorial(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return deduplicarPorRecurso(historiaLecturaRepository.findHistorialActivosByUsuario(usuario)).stream()
                .map(this::toHistorialDto)
                .toList();
    }

    @Override
    public LecturaAnotacionesResponseDTO obtenerAnotaciones(Long recursoId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        List<HistoriaLectura> filas = historiaLecturaRepository
                .findAllByUsuarioAndRecursoOrderByFechaLecturaDesc(usuario, recurso);
        if (filas.isEmpty()) {
            return new LecturaAnotacionesResponseDTO(null);
        }
        HistoriaLectura hl = filas.size() == 1 ? filas.get(0) : obtenerOConsolidarHistoria(usuario, recurso);
        return new LecturaAnotacionesResponseDTO(hl.getResaltadosJson());
    }

    @Override
    @Transactional
    public MensajeResponseDTO guardarAnotaciones(Long recursoId, Long usuarioId, LecturaAnotacionesRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        HistoriaLectura hl = obtenerOConsolidarHistoria(usuario, recurso);

        hl.setResaltadosJson(request.anotacionesJson());
        hl.setFechaLectura(LocalDateTime.now());
        historiaLecturaRepository.save(hl);
        return new MensajeResponseDTO(true, "Anotaciones guardadas");
    }

    @Override
    public List<HistoriaLecturaResponseDTO> listarContinuarLeyendo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<HistoriaLectura> unicos = deduplicarPorRecurso(
                historiaLecturaRepository.findContinuarLeyendo(usuario, PageRequest.of(0, MAX_CONTINUAR * 3)));
        return unicos.stream()
                .limit(MAX_CONTINUAR)
                .map(this::toHistorialDto)
                .toList();
    }

    /**
     * Una fila por usuario+recurso. Si hay duplicados (carrera progreso/anotaciones o historial antiguo),
     * se fusionan en la más reciente y se eliminan el resto.
     */
    private HistoriaLectura obtenerOConsolidarHistoria(Usuario usuario, Recurso recurso) {
        List<HistoriaLectura> filas = historiaLecturaRepository
                .findAllByUsuarioAndRecursoOrderByFechaLecturaDesc(usuario, recurso);
        if (filas.isEmpty()) {
            return HistoriaLectura.builder()
                    .usuario(usuario)
                    .recurso(recurso)
                    .fechaLectura(LocalDateTime.now())
                    .build();
        }
        HistoriaLectura principal = filas.get(0);
        for (int i = 1; i < filas.size(); i++) {
            fusionarHistorialEn(principal, filas.get(i));
            historiaLecturaRepository.delete(filas.get(i));
        }
        return principal;
    }

    private static void fusionarHistorialEn(HistoriaLectura destino, HistoriaLectura origen) {
        if (origen.getUltimaPagina() != null) {
            if (destino.getUltimaPagina() == null || origen.getUltimaPagina() > destino.getUltimaPagina()) {
                destino.setUltimaPagina(origen.getUltimaPagina());
            }
        }
        if (origen.getTotalPaginas() != null) {
            if (destino.getTotalPaginas() == null || origen.getTotalPaginas() > destino.getTotalPaginas()) {
                destino.setTotalPaginas(origen.getTotalPaginas());
            }
        }
        if (origen.getResaltadosJson() != null && !origen.getResaltadosJson().isBlank()) {
            if (destino.getResaltadosJson() == null || destino.getResaltadosJson().isBlank()) {
                destino.setResaltadosJson(origen.getResaltadosJson());
            }
        }
        if (origen.getFechaLectura() != null
                && (destino.getFechaLectura() == null || origen.getFechaLectura().isAfter(destino.getFechaLectura()))) {
            destino.setFechaLectura(origen.getFechaLectura());
        }
    }

    /** Mantiene la entrada más reciente por recurso (para listados sin tarjetas duplicadas). */
    private static List<HistoriaLectura> deduplicarPorRecurso(List<HistoriaLectura> filas) {
        Map<Long, HistoriaLectura> porRecurso = new LinkedHashMap<>();
        for (HistoriaLectura hl : filas) {
            Long recursoId = hl.getRecurso().getRecursoId();
            HistoriaLectura actual = porRecurso.get(recursoId);
            if (actual == null || esMasReciente(hl, actual)) {
                porRecurso.put(recursoId, hl);
            }
        }
        return new ArrayList<>(porRecurso.values());
    }

    private static boolean esMasReciente(HistoriaLectura a, HistoriaLectura b) {
        if (a.getFechaLectura() == null) {
            return false;
        }
        if (b.getFechaLectura() == null) {
            return true;
        }
        return a.getFechaLectura().isAfter(b.getFechaLectura());
    }

    private HistoriaLecturaResponseDTO toHistorialDto(HistoriaLectura hl) {
        Recurso r = hl.getRecurso();
        return new HistoriaLecturaResponseDTO(
                hl.getHistoriasLecturasId(),
                r.getRecursoId(),
                r.getNombre(),
                r.getAutor(),
                r.getUrlPortada(),
                hl.getFechaLectura(),
                hl.getUltimaPagina(),
                hl.getTotalPaginas(),
                calcularPorcentaje(hl.getUltimaPagina(), hl.getTotalPaginas())
        );
    }

    private LecturaProgresoResponseDTO toProgresoDto(HistoriaLectura hl) {
        return new LecturaProgresoResponseDTO(
                hl.getUltimaPagina(),
                hl.getTotalPaginas(),
                calcularPorcentaje(hl.getUltimaPagina(), hl.getTotalPaginas())
        );
    }

    private static Integer calcularPorcentaje(Integer ultima, Integer total) {
        if (ultima == null || total == null || total <= 0) {
            return null;
        }
        return (int) Math.min(100, Math.round(100.0 * ultima / total));
    }
}
