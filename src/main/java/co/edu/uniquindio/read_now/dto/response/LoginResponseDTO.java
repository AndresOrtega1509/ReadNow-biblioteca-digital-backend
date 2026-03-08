package co.edu.uniquindio.read_now.dto.response;

public record LoginResponseDTO(
        String token,
        String email,
        String rol,
        String nombre,
        String username,
        Long usuarioId,
        SesionConfigResponseDTO sesionConfig
) {}
