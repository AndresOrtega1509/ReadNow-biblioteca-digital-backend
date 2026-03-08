package co.edu.uniquindio.read_now.config;

import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class InactividadScheduler {

    private final IUsuarioRepository usuarioRepository;
    private final IEmailService emailService;

    @Value("${app.inactivity.notification-days}")
    private int notificationDays;

    @Scheduled(cron = "0 0 9 * * ?")
    public void verificarInactividad() {
        log.info("Ejecutando verificación de inactividad de usuarios...");

        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(notificationDays);

        List<Usuario> usuariosInactivos = usuarioRepository
                .findByUltimoAccesoBeforeAndActivoAndRolNombre(fechaLimite, "S", "LECTOR");

        for (Usuario usuario : usuariosInactivos) {
            try {
                emailService.enviarCorreoInactividad(usuario.getEmail(), usuario.getNombre());
                log.info("Correo de inactividad enviado a: {}", usuario.getEmail());
            } catch (Exception e) {
                log.error("Error al enviar correo de inactividad a {}: {}", usuario.getEmail(), e.getMessage());
            }
        }

        log.info("Verificación completada. {} usuarios inactivos notificados.", usuariosInactivos.size());
    }
}
