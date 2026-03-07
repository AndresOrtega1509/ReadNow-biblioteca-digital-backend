package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.response.FavoritoResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;

import java.util.List;

public interface IFavoritoService {

    MensajeResponseDTO agregarFavorito(Long recursoId, Long usuarioId);

    MensajeResponseDTO eliminarFavorito(Long favoritoId, Long usuarioId);

    List<FavoritoResponseDTO> listarFavoritos(Long usuarioId);
}
