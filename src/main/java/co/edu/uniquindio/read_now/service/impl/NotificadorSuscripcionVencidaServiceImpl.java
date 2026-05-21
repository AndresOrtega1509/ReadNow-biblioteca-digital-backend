package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IEmailService;
import co.edu.uniquindio.read_now.service.INotificadorSuscripcionVencidaService;
import co.edu.uniquindio.read_now.util.SuscripcionAccesoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            if (SuscripcionAccesoUtil.esSuscripcionActiva(usuario)) return;
            if (!SuscripcionAccesoUtil.haTenidoSuscripcion(usuario)) return;

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

}
