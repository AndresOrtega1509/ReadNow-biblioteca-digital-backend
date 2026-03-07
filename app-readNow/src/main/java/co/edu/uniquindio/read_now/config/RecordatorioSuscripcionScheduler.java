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
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordatorioSuscripcionScheduler {

    private final IUsuarioRepository usuarioRepository;
    private final IEmailService emailService;

    /** Ejecuta diariamente a las 10:00. Envía recordatorios a 5 y 1 día de vencimiento. */
    @Scheduled(cron = "0 0 10 * * ?")
    @Transactional
    public void enviarRecordatoriosSuscripcion() {
        log.info("Ejecutando recordatorios de suscripción por vencer...");

        LocalDate hoy = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();

        List<Usuario> lectoresActivos = usuarioRepository.findLectoresConSuscripcionActiva(hoy, ahora);

        for (Usuario usuario : lectoresActivos) {
            LocalDate fechaFin = usuario.getFinSuscripcionAt() != null
                    ? usuario.getFinSuscripcionAt().toLocalDate()
                    : usuario.getFinSuscripcion();
            if (fechaFin == null) continue;

            long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaFin);
            if (diasRestantes < 1) continue; // No recordar si vence hoy o ya pasó

            String nombreCompleto = ((usuario.getNombre() != null ? usuario.getNombre() : "") + " "
                    + (usuario.getApellido() != null ? usuario.getApellido() : "")).trim();
            if (nombreCompleto.isBlank()) nombreCompleto = usuario.getUsername() != null ? usuario.getUsername() : "Lector";

            try {
                if (diasRestantes == 5 && !Boolean.TRUE.equals(usuario.getRecordatorio5DiasEnviado())) {
                    emailService.enviarRecordatorioSuscripcionPorVencer(usuario.getEmail(), nombreCompleto, 5);
                    usuario.setRecordatorio5DiasEnviado(true);
                    usuarioRepository.save(usuario);
                    log.info("Recordatorio 5 días enviado a: {}", usuario.getEmail());
                } else if (diasRestantes == 1 && !Boolean.TRUE.equals(usuario.getRecordatorio1DiaEnviado())) {
                    emailService.enviarRecordatorioSuscripcionPorVencer(usuario.getEmail(), nombreCompleto, 1);
                    usuario.setRecordatorio1DiaEnviado(true);
                    usuarioRepository.save(usuario);
                    log.info("Recordatorio 1 día enviado a: {}", usuario.getEmail());
                }
            } catch (Exception e) {
                log.error("Error al enviar recordatorio a {}: {}", usuario.getEmail(), e.getMessage());
            }
        }

        log.info("Recordatorios de suscripción completados.");
    }
}
