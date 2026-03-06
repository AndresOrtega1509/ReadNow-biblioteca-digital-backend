package co.edu.uniquindio.read_now.dto.response;

public record LoginResponseDTO(
        String token,
        String email,
        String rol,
        String nombre,
        Long usuarioId,
        SesionConfigResponseDTO sesionConfig
) {}
