package co.edu.uniquindio.read_now.config;

import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuscripcionVencidaScheduler {

    private final IUsuarioRepository usuarioRepository;
    private final IEmailService emailService;

    /** Ejecuta cada minuto. Notifica por correo a lectores con suscripción vencida que aún no han sido notificados. */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void notificarSuscripcionesVencidas() {
        log.info("Ejecutando notificación de suscripciones vencidas...");

        LocalDate hoy = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();

        List<Usuario> usuarios = usuarioRepository.findLectoresConSuscripcionVencidaNoNotificados(hoy, ahora);

        for (Usuario usuario : usuarios) {
            try {
                String nombreCompleto = ((usuario.getNombre() != null ? usuario.getNombre() : "") + " " + (usuario.getApellido() != null ? usuario.getApellido() : "")).trim();
                if (nombreCompleto.isBlank()) nombreCompleto = usuario.getUsername() != null ? usuario.getUsername() : "Lector";

                emailService.enviarCorreoSuscripcionVencida(usuario.getEmail(), nombreCompleto);
                usuario.setSuscripcionVencidaNotificada(true);
                usuarioRepository.save(usuario);
                log.info("Correo de suscripción vencida enviado a: {}", usuario.getEmail());
            } catch (Exception e) {
                log.error("Error al enviar correo de suscripción vencida a {}: {}", usuario.getEmail(), e.getMessage());
            }
        }

        log.info("Notificación completada. {} lectores con suscripción vencida notificados.", usuarios.size());
    }
}
