package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.ActualizarPerfilRequestDTO;
import co.edu.uniquindio.read_now.dto.request.CambiarPasswordRequestDTO;
import co.edu.uniquindio.read_now.dto.response.UsuarioResponseDTO;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.INotificadorSuscripcionVencidaService;
import co.edu.uniquindio.read_now.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final INotificadorSuscripcionVencidaService notificadorSuscripcionVencida;

    @Override
    public UsuarioResponseDTO obtenerPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean suscripcionActiva = esSuscripcionActiva(usuario);

        // Si la suscripción está vencida y no se ha notificado, enviar correo de forma asíncrona
        if (!suscripcionActiva && !Boolean.TRUE.equals(usuario.getSuscripcionVencidaNotificada())) {
            notificadorSuscripcionVencida.notificarSiCorresponde(usuarioId);
        }

        return new UsuarioResponseDTO(
                usuario.getUsuarioId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getUsername(),
                usuario.getFechaRegistro(),
                usuario.getRol().getNombre(),
                usuario.getInicioSuscripcion(),
                usuario.getFinSuscripcion(),
                usuario.getFinSuscripcionAt(),
                suscripcionActiva,
                usuario.getTwoFactorActivo()
        );
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarPerfil(Long usuarioId, ActualizarPerfilRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getUsername().equals(request.username())
                && usuarioRepository.existsByUsername(request.username())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());
        usuario.setUsername(request.username());
        usuarioRepository.save(usuario);

        return obtenerPerfil(usuarioId);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long usuarioId, CambiarPasswordRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.contraseñaActual(), usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }
        if (!request.nuevaPassword().equals(request.confirmarPassword())) {
            throw new IllegalArgumentException("La nueva contraseña y la confirmación no coinciden");
        }

        usuario.setPassword(passwordEncoder.encode(request.nuevaPassword()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarVerificacionDosPasos(Long usuarioId, boolean activo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setTwoFactorActivo(activo);
        usuarioRepository.save(usuario);
        return obtenerPerfil(usuarioId);
    }
    @Override
    @Transactional
    public void actualizarUltimoAcceso(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
    @Override
    public boolean puedeAccederAlCatalogo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if ("ADMIN".equals(usuario.getRol().getNombre())) return true;
        return esSuscripcionActiva(usuario);
    }

    private boolean esSuscripcionActiva(Usuario usuario) {
        LocalDateTime ahora = LocalDateTime.now();
        if (usuario.getFinSuscripcionAt() != null) {
            return ahora.isBefore(usuario.getFinSuscripcionAt());
        }
        return usuario.getFinSuscripcion() != null
                && !usuario.getFinSuscripcion().isBefore(LocalDate.now());
    }
    @Override
    public boolean tieneSuscripcionActiva(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return esSuscripcionActiva(usuario);
    }


}
