package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.CalificacionRequestDTO;
import co.edu.uniquindio.read_now.dto.response.CalificacionResponseDTO;
import co.edu.uniquindio.read_now.model.Calificacion;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.ICalificacionRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.ICalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements ICalificacionService {

    private final ICalificacionRepository calificacionRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IRecursoRepository recursoRepository;

    @Override
    @Transactional
    public CalificacionResponseDTO calificar(CalificacionRequestDTO request, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Recurso recurso = recursoRepository.findById(request.recursoId())
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        Optional<Calificacion> existente = calificacionRepository.findByUsuarioAndRecurso(usuario, recurso);

        Calificacion calificacion;
        if (existente.isPresent()) {
            calificacion = existente.get();
            calificacion.setValor(request.valor());
        } else {
            calificacion = Calificacion.builder()
                    .usuario(usuario)
                    .recurso(recurso)
                    .valor(request.valor())
                    .build();
        }

        calificacion = calificacionRepository.save(calificacion);

        return new CalificacionResponseDTO(
                calificacion.getCalificacionId(),
                recurso.getRecursoId(),
                usuario.getUsuarioId(),
                usuario.getNombre() + " " + usuario.getApellido(),
                calificacion.getValor()
        );
    }

    @Override
    public Double obtenerPromedioCalificacion(Long recursoId) {
        return calificacionRepository.findPromedioByRecursoId(recursoId);
    }

    @Override
    public int obtenerMiCalificacion(Long recursoId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        Recurso recurso = recursoRepository.findById(recursoId).orElse(null);
        if (usuario == null || recurso == null) return 0;
        return calificacionRepository.findByUsuarioAndRecurso(usuario, recurso)
                .map(Calificacion::getValor)
                .orElse(0);
    }

    @Override
    public List<CalificacionResponseDTO> obtenerCalificacionesPorRecurso(Long recursoId) {
        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        return calificacionRepository.findByRecurso(recurso).stream()
                .map(c -> new CalificacionResponseDTO(
                        c.getCalificacionId(),
                        recurso.getRecursoId(),
                        c.getUsuario().getUsuarioId(),
                        c.getUsuario().getNombre() + " " + c.getUsuario().getApellido(),
                        c.getValor()
                ))
                .toList();
    }
}
