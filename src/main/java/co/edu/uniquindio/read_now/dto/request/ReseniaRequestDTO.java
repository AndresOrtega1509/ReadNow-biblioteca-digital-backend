package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReseniaRequestDTO(
        @NotNull(message = "El ID del recurso es obligatorio")
        Long recursoId,

        @NotBlank(message = "La descripción de la reseña es obligatoria")
        String descripcion
) {}
