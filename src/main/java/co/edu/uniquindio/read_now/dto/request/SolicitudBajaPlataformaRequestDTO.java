package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Solicitud del usuario para cancelar su inscripción: desactiva la cuenta sin eliminar datos.
 */
public record SolicitudBajaPlataformaRequestDTO(
        @Size(max = 2000, message = "El motivo no puede superar 2000 caracteres")
        String motivo
) {}
