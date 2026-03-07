package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.CategoriaRecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.CategoriaRecursoResponseDTO;
import co.edu.uniquindio.read_now.model.CategoriaRecurso;
import co.edu.uniquindio.read_now.repository.ICategoriaRecursoRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.service.ICategoriaRecursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaRecursoServiceImpl implements ICategoriaRecursoService {

    private final ICategoriaRecursoRepository categoriaRecursoRepository;
    private final IRecursoRepository recursoRepository;

    @Override
    public List<CategoriaRecursoResponseDTO> listarTodas() {
        return categoriaRecursoRepository.findAll().stream()
                .map(c -> new CategoriaRecursoResponseDTO(c.getCategoriaRecursoId(), c.getNombre()))
                .toList();
    }

    @Override
    @Transactional
    public CategoriaRecursoResponseDTO crear(CategoriaRecursoRequestDTO request) {
        String nombre = request.nombre().trim();
        if (categoriaRecursoRepository.findByNombre(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre \"" + nombre + "\".");
        }
        CategoriaRecurso c = categoriaRecursoRepository.save(
                CategoriaRecurso.builder().nombre(nombre).build());
        return new CategoriaRecursoResponseDTO(c.getCategoriaRecursoId(), c.getNombre());
    }

    @Override
    @Transactional
    public CategoriaRecursoResponseDTO actualizar(Long id, CategoriaRecursoRequestDTO request) {
        CategoriaRecurso c = categoriaRecursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));
        String nombre = request.nombre().trim();
        categoriaRecursoRepository.findByNombre(nombre).ifPresent(existing -> {
            if (!existing.getCategoriaRecursoId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otra categoría con el nombre \"" + nombre + "\".");
            }
        });
        c.setNombre(nombre);
        c = categoriaRecursoRepository.save(c);
        return new CategoriaRecursoResponseDTO(c.getCategoriaRecursoId(), c.getNombre());
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        CategoriaRecurso c = categoriaRecursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));
        recursoRepository.desvincularPorCategoria(id);
        categoriaRecursoRepository.delete(c);
    }
}
