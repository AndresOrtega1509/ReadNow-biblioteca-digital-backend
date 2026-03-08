package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRecursoRequestDTO(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        String nombre
) {}
