package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilRequestDTO(
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    String apellido,

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 10, max = 10, message = "El teléfono debe tener exactamente 10 dígitos")
    @Pattern(regexp = "\\d{10}", message = "El teléfono solo debe contener 10 dígitos numéricos")
    String telefono,

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 30, message = "El username debe tener entre 3 y 30 caracteres")
    String username

) {}
