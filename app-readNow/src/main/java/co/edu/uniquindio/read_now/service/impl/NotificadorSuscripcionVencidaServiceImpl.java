package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IEmailService;
import co.edu.uniquindio.read_now.service.INotificadorSuscripcionVencidaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificadorSuscripcionVencidaServiceImpl implements INotificadorSuscripcionVencidaService {

    private final IUsuarioRepository usuarioRepository;
    private final IEmailService emailService;

    @Async
    @Override
    @Transactional
    public void notificarSiCorresponde(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            if (usuario == null) return;
            if (!"LECTOR".equals(usuario.getRol().getNombre())) return;
            if (Boolean.TRUE.equals(usuario.getSuscripcionVencidaNotificada())) return;
            if (esSuscripcionActiva(usuario)) return;

            String nombreCompleto = ((usuario.getNombre() != null ? usuario.getNombre() : "") + " "
                    + (usuario.getApellido() != null ? usuario.getApellido() : "")).trim();
            if (nombreCompleto.isBlank()) nombreCompleto = usuario.getUsername() != null ? usuario.getUsername() : "Lector";

            emailService.enviarCorreoSuscripcionVencida(usuario.getEmail(), nombreCompleto);
            usuario.setSuscripcionVencidaNotificada(true);
            usuarioRepository.save(usuario);
            log.info("Correo de suscripción vencida enviado a: {} (perfil cargado)", usuario.getEmail());
        } catch (Exception e) {
            log.error("Error al notificar suscripción vencida para usuario {}: {}", usuarioId, e.getMessage());
        }
    }

    private boolean esSuscripcionActiva(Usuario usuario) {
        LocalDateTime ahora = LocalDateTime.now();
        if (usuario.getFinSuscripcionAt() != null) {
            return ahora.isBefore(usuario.getFinSuscripcionAt());
        }
        return usuario.getFinSuscripcion() != null
                && !usuario.getFinSuscripcion().isBefore(LocalDate.now());
    }
}
