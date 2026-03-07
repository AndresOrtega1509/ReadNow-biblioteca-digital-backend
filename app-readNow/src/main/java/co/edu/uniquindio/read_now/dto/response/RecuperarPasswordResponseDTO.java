package co.edu.uniquindio.read_now.dto.response;

/**
 * Respuesta al solicitar recuperación de contraseña (paso 1).
 * Si el usuario existe y tiene teléfono, incluye el número enmascarado (últimos 4 como ****) para mostrarlo en el paso 2.
 */
public record RecuperarPasswordResponseDTO(
        boolean exitoso,
        String mensaje,
        String telefonoEnmascarado
) {}
