package co.edu.uniquindio.read_now.dto.response;

/**
 * Respuesta del login. Si 2FA está desactivado (desarrollo), viene token y datos de usuario.
 * Si 2FA está activado, solo viene mensaje y el cliente debe ir a verificar código.
 */
public record LoginResultDTO(
        boolean exitoso,
        String mensaje,
        String token,
        String email,
        String rol,
        String nombre,
        Long usuarioId,
        SesionConfigResponseDTO sesionConfig
) {
    /** Cuando 2FA está activado: solo mensaje para pedir código */
    public static LoginResultDTO conMensaje(boolean exitoso, String mensaje) {
        return new LoginResultDTO(exitoso, mensaje, null, null, null, null, null, null);
    }

    /** Cuando 2FA está desactivado: token y datos para entrar directo */
    public static LoginResultDTO conToken(LoginResponseDTO login) {
        return new LoginResultDTO(
                true,
                null,
                login.token(),
                login.email(),
                login.rol(),
                login.nombre(),
                login.usuarioId(),
                login.sesionConfig()
        );
    }
}
