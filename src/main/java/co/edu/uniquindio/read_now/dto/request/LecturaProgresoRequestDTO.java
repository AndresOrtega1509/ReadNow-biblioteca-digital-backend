package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Progreso de lectura en un PDF (páginas 1-based).
 */
public record LecturaProgresoRequestDTO(
        @NotNull(message = "La página actual es obligatoria")
        @Min(value = 1, message = "La página actual debe ser al menos 1")
        Integer ultimaPagina,

        /** Opcional; si se envía, debe ser ≥ {@code ultimaPagina}. */
        @Min(value = 1, message = "El total de páginas debe ser al menos 1")
        Integer totalPaginas
) {}
