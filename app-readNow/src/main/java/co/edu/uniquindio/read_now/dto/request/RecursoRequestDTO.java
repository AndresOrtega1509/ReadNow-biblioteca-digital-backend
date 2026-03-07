package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RecursoRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String autor,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotBlank(message = "El idioma es obligatorio")
        String idioma,

        LocalDate fechaPublicacion,

        @NotNull(message = "El tipo de recurso es obligatorio")
        Long tipoRecursoId,

        Long categoriaRecursoId,

        String urlPortada

        
) {}
