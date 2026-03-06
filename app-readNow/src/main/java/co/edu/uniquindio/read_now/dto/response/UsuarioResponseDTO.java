package co.edu.uniquindio.read_now.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long usuarioId,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String username,
        LocalDate fechaRegistro,
        String rol,
        LocalDate inicioSuscripcion,
        LocalDate finSuscripcion,
        LocalDateTime finSuscripcionAt,
        boolean suscripcionActiva,
        Boolean twoFactorActivo
) {}
