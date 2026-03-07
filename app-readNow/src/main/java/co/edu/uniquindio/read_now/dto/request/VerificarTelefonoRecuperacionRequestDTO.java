package co.edu.uniquindio.read_now.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerificarTelefonoRecuperacionRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        @NotBlank(message = "Los últimos 4 dígitos son obligatorios")
        @Size(min = 4, max = 4, message = "Deben ser exactamente 4 dígitos")
        @Pattern(regexp = "\\d{4}", message = "Solo se permiten 4 dígitos numéricos")
        String ultimos4Digitos
) {}
