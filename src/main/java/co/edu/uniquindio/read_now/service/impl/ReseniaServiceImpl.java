package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.ReseniaRequestDTO;
import co.edu.uniquindio.read_now.dto.response.ReseniaResponseDTO;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Resenia;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.IReseniaRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IReseniaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReseniaServiceImpl implements IReseniaService {

    private final IReseniaRepository reseniaRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IRecursoRepository recursoRepository;

    @Override
    @Transactional
    public ReseniaResponseDTO crearResenia(ReseniaRequestDTO request, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Recurso recurso = recursoRepository.findById(request.recursoId())
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        Resenia resenia = Resenia.builder()
                .recurso(recurso)
                .usuario(usuario)
                .descripcion(request.descripcion())
                .fechaCreacion(LocalDateTime.now())
                .build();

        resenia = reseniaRepository.save(resenia);

        return new ReseniaResponseDTO(
                resenia.getReseniaId(),
                recurso.getRecursoId(),
                usuario.getUsername(),
                resenia.getDescripcion(),
                resenia.getFechaCreacion()
        );
    }

    @Override
    public List<ReseniaResponseDTO> obtenerReseniasPorRecurso(Long recursoId) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        return reseniaRepository.findByRecursoOrderByFechaCreacionDesc(recurso).stream()
                .map(r -> new ReseniaResponseDTO(
                        r.getReseniaId(),
                        recurso.getRecursoId(),
                        r.getUsuario().getUsername(),
                        r.getDescripcion(),
                        r.getFechaCreacion()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void eliminarReseniaComoAdmin(Long reseniaId) {
        int filas = reseniaRepository.deleteByReseniaId(reseniaId);
        if (filas == 0) {
            throw new RuntimeException("Reseña no encontrada");
        }
    }
}
