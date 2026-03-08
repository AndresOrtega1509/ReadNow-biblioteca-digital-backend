package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TipoRecursoRequestDTO(
        @NotBlank(message = "El nombre del tipo de recurso es obligatorio")
        String nombre
) {}
