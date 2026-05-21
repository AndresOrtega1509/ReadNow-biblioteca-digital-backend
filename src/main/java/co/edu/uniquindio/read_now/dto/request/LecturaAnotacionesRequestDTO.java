package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Resaltados y anotaciones del visor PDF (JSON serializado de ngx-extended-pdf-viewer).
 */
public record LecturaAnotacionesRequestDTO(
        @NotNull
        @Size(max = 2_000_000)
        String anotacionesJson
) {}
