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
        boolean puedeActivarPruebaGratuita,
        Boolean twoFactorActivo,
        /** Nombre del plan de pago, o "Prueba gratuita" si hay acceso sin plan asignado. Null si no aplica. */
        String nombrePlanSuscripcion,
        /** True si la cuenta está habilitada para ingresar ({@code activo == "S"}). */
        boolean cuentaActiva
) {}
