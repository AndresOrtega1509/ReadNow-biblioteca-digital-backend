package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CambiarPasswordRequestDTO(
        @NotBlank(message = "La contraseña actual es obligatoria")
        String contraseñaActual,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{6,}$",
                message = "La contraseña debe tener al menos 6 caracteres, una mayúscula, una letra, un número y un carácter especial"
        )
        String nuevaPassword,

        @NotBlank(message = "Debes confirmar la nueva contraseña")
        String confirmarPassword
) {}
