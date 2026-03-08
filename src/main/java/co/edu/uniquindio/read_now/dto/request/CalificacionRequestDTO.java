package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CalificacionRequestDTO(
        @NotNull(message = "El ID del recurso es obligatorio")
        Long recursoId,

        @NotNull(message = "El valor de la calificación es obligatorio")
        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        Integer valor
) {}
