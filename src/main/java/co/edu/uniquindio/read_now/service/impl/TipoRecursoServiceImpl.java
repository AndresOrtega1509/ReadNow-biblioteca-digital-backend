package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.TipoRecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.TipoRecursoResponseDTO;
import co.edu.uniquindio.read_now.model.TipoRecurso;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.ITipoRecursoRepository;
import co.edu.uniquindio.read_now.service.ITipoRecursoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TipoRecursoServiceImpl implements ITipoRecursoService {

    private static final String[] TIPOS_DEFAULT = {"Libro", "Tesis", "Revista", "Artículo", "Manual"};

    private final ITipoRecursoRepository tipoRecursoRepository;
    private final IRecursoRepository recursoRepository;

    @Override
    @Transactional
    public List<TipoRecursoResponseDTO> listarTodos() {
        List<TipoRecurso> todos = tipoRecursoRepository.findAll();
        if (todos.isEmpty()) {
            log.info("Tabla de tipos vacía, inicializando tipos por defecto...");
            inicializarTiposPorDefecto();
            todos = tipoRecursoRepository.findAll();
        }
        return todos.stream()
                .map(t -> new TipoRecursoResponseDTO(t.getTipoRecursoId(), t.getNombre()))
                .toList();
    }

    @Transactional
    protected void inicializarTiposPorDefecto() {
        for (String nombre : TIPOS_DEFAULT) {
            if (tipoRecursoRepository.findByNombre(nombre).isEmpty()) {
                tipoRecursoRepository.save(TipoRecurso.builder().nombre(nombre).build());
                log.info("Tipo de recurso creado: {}", nombre);
            }
        }
    }

    @Override
    @Transactional
    public TipoRecursoResponseDTO crear(TipoRecursoRequestDTO request) {
        String nombre = request.nombre().trim();
        if (tipoRecursoRepository.findByNombre(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe un tipo de recurso con el nombre \"" + nombre + "\".");
        }
        TipoRecurso t = tipoRecursoRepository.save(
                TipoRecurso.builder().nombre(nombre).build());
        return new TipoRecursoResponseDTO(t.getTipoRecursoId(), t.getNombre());
    }

    @Override
    @Transactional
    public TipoRecursoResponseDTO actualizar(Long id, TipoRecursoRequestDTO request) {
        TipoRecurso t = tipoRecursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de recurso no encontrado."));
        String nombre = request.nombre().trim();
        tipoRecursoRepository.findByNombre(nombre).ifPresent(existing -> {
            if (!existing.getTipoRecursoId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro tipo de recurso con el nombre \"" + nombre + "\".");
            }
        });
        t.setNombre(nombre);
        t = tipoRecursoRepository.save(t);
        return new TipoRecursoResponseDTO(t.getTipoRecursoId(), t.getNombre());
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        TipoRecurso t = tipoRecursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de recurso no encontrado."));
        recursoRepository.desvincularPorTipo(id);
        tipoRecursoRepository.delete(t);
    }
}
