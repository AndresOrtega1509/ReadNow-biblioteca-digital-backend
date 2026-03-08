package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.response.FavoritoResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.model.Favorito;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IFavoritoRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IFavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritoServiceImpl implements IFavoritoService {

    private final IFavoritoRepository favoritoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IRecursoRepository recursoRepository;

    @Override
    @Transactional
    public MensajeResponseDTO agregarFavorito(Long recursoId, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Recurso recurso = recursoRepository.findById(recursoId)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        if (favoritoRepository.existsByUsuarioAndRecurso(usuario, recurso)) {
            return new MensajeResponseDTO(false, "El recurso ya está en tus favoritos");
        }

        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .recurso(recurso)
                .build();

        favoritoRepository.save(favorito);
        return new MensajeResponseDTO(true, "Recurso agregado a favoritos");
    }

    @Override
    @Transactional
    public MensajeResponseDTO eliminarFavorito(Long favoritoId, Long usuarioId) {
        Favorito favorito = favoritoRepository.findById(favoritoId)
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));

        if (!favorito.getUsuario().getUsuarioId().equals(usuarioId)) {
            return new MensajeResponseDTO(false, "No tienes permiso para eliminar este favorito");
        }

        favoritoRepository.delete(favorito);
        return new MensajeResponseDTO(true, "Recurso eliminado de favoritos");
    }

    @Override
    public List<FavoritoResponseDTO> listarFavoritos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return favoritoRepository.findByUsuario(usuario).stream()
                .map(f -> new FavoritoResponseDTO(
                        f.getFavoritoId(),
                        f.getRecurso().getRecursoId(),
                        f.getRecurso().getNombre(),
                        f.getRecurso().getAutor(),
                        f.getRecurso().getUrlArchivo(),
                        f.getRecurso().getUrlPortada()
                ))
                .toList();
    }
}
