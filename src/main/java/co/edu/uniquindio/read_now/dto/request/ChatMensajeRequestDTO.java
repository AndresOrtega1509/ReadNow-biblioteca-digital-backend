package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMensajeRequestDTO(
        @NotBlank(message = "El mensaje no puede estar vacío")
        @Size(max = 2000, message = "El mensaje es demasiado largo")
        String mensaje,
        @Size(max = 500, message = "El contexto de página es demasiado largo")
        String paginaContexto
) {}
