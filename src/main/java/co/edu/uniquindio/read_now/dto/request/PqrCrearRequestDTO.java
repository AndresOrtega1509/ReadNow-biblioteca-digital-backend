package co.edu.uniquindio.read_now.dto.request;

import co.edu.uniquindio.read_now.model.enums.TipoPqr;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PqrCrearRequestDTO(
        @NotNull(message = "El tipo de PQR es obligatorio")
        TipoPqr tipo,

        @NotBlank(message = "El asunto es obligatorio")
        @Size(max = 200, message = "El asunto no puede superar 200 caracteres")
        String asunto,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 5000, message = "La descripción no puede superar 5000 caracteres")
        String descripcion
) {}
