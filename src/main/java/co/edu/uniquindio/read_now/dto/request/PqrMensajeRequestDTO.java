package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PqrMensajeRequestDTO(
        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 5000, message = "El mensaje no puede superar 5000 caracteres")
        String contenido
) {}
