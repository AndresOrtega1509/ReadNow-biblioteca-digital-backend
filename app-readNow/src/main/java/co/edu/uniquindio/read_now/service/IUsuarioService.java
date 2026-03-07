package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.ActualizarPerfilRequestDTO;
import co.edu.uniquindio.read_now.dto.request.CambiarPasswordRequestDTO;
import co.edu.uniquindio.read_now.dto.response.UsuarioResponseDTO;

public interface IUsuarioService {

    UsuarioResponseDTO obtenerPerfil(Long usuarioId);

    UsuarioResponseDTO actualizarPerfil(Long usuarioId, ActualizarPerfilRequestDTO request);

    void cambiarPassword(Long usuarioId, CambiarPasswordRequestDTO request);

    /** Activa o desactiva la verificación en dos pasos para el usuario. */
    UsuarioResponseDTO actualizarVerificacionDosPasos(Long usuarioId, boolean activo);
    void actualizarUltimoAcceso(String email);
    boolean puedeAccederAlCatalogo(Long usuarioId);

}
