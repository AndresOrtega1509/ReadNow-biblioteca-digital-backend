package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.response.HistoriaLecturaResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.model.HistoriaLectura;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IHistoriaLecturaRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IHistoriaLecturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoriaLecturaServiceImpl implements IHistoriaLecturaService {

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

        Optional<HistoriaLectura> existente = historiaLecturaRepository.findByUsuarioAndRecurso(usuario, recurso);

        if (existente.isPresent()) {
            HistoriaLectura historia = existente.get();
            historia.setFechaLectura(LocalDateTime.now());
            historiaLecturaRepository.save(historia);
        } else {
            HistoriaLectura historia = HistoriaLectura.builder()
                    .usuario(usuario)
                    .recurso(recurso)
                    .fechaLectura(LocalDateTime.now())
                    .build();
            historiaLecturaRepository.save(historia);
        }

        return new MensajeResponseDTO(true, "Lectura registrada exitosamente");
    }

    @Override
    public List<HistoriaLecturaResponseDTO> obtenerHistorial(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return historiaLecturaRepository.findByUsuarioOrderByFechaLecturaDesc(usuario).stream()
                .map(hl -> new HistoriaLecturaResponseDTO(
                        hl.getHistoriasLecturasId(),
                        hl.getRecurso().getRecursoId(),
                        hl.getRecurso().getNombre(),
                        hl.getRecurso().getAutor(),
                        hl.getFechaLectura()
                ))
                .toList();
    }
}
