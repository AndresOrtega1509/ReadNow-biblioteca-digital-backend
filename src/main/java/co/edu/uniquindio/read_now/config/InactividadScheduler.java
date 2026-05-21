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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void verificarInactividad() {
        log.info("Ejecutando verificación de inactividad (umbral: {} días)...", notificationDays);

        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(notificationDays);

        List<Usuario> usuariosInactivos = usuarioRepository.findLectoresInactivosSinRecordatorio(fechaLimite);

        int enviados = 0;
        for (Usuario usuario : usuariosInactivos) {
            try {
                String nombre = usuario.getNombre() != null ? usuario.getNombre() : usuario.getUsername();
                emailService.enviarCorreoInactividad(usuario.getEmail(), nombre, notificationDays);
                usuario.setRecordatorioInactividadEnviado(true);
                usuarioRepository.save(usuario);
                enviados++;
                log.info("Recordatorio único de inactividad enviado a: {}", usuario.getEmail());
            } catch (Exception e) {
                log.error("Error al enviar correo de inactividad a {}: {}", usuario.getEmail(), e.getMessage());
            }
        }

        log.info("Verificación completada. {} correo(s) de inactividad enviado(s) de {} candidato(s).",
                enviados, usuariosInactivos.size());
    }
}
