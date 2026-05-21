package co.edu.uniquindio.read_now.dto.request;

import co.edu.uniquindio.read_now.model.enums.EstadoPqr;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PqrActualizarEstadoRequestDTO(
        @NotNull(message = "El estado es obligatorio")
        EstadoPqr estado,

        @Size(max = 5000, message = "El mensaje no puede superar 5000 caracteres")
        String mensaje
) {}
