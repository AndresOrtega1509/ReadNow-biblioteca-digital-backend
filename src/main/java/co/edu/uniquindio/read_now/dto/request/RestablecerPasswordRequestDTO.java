package co.edu.uniquindio.read_now.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RestablecerPasswordRequestDTO(
        @NotBlank(message = "El token es obligatorio")
        String token,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{6,}$",
                message = "La contraseña debe tener al menos 6 caracteres, una mayúscula, una letra, un número y un carácter especial"
        )
        String nuevaPassword
) {}
