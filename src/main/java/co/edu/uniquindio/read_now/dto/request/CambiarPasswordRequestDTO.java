package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarPasswordRequestDTO(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String contraseñaActual,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String nuevaPassword,

        @NotBlank(message = "Debes confirmar la nueva contraseña")
        String confirmarPassword
) {}
