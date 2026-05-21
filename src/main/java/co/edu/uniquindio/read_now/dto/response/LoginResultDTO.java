package co.edu.uniquindio.read_now.dto.response;

/**
 * Respuesta del login. Si 2FA está desactivado (desarrollo), viene token y datos de usuario.
 * Si 2FA está activado, solo viene mensaje y el cliente debe ir a verificar código.
 * {@code cuentaInactiva} indica credenciales correctas pero cuenta desactivada (reactivación en el cliente).
 */
public record LoginResultDTO(
        boolean exitoso,
        String mensaje,
        String token,
        String email,
        String rol,
        String nombre,
        Long usuarioId,
        SesionConfigResponseDTO sesionConfig,
        boolean cuentaInactiva
) {
    /** Cuando 2FA está activado: solo mensaje para pedir código */
    public static LoginResultDTO conMensaje(boolean exitoso, String mensaje) {
        return new LoginResultDTO(exitoso, mensaje, null, null, null, null, null, null, false);
    }

    /** Credenciales válidas pero cuenta inactiva: el cliente puede ofrecer reactivación. */
    public static LoginResultDTO conCuentaInactiva(String mensaje) {
        return new LoginResultDTO(false, mensaje, null, null, null, null, null, null, true);
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
                login.sesionConfig(),
                false
        );
    }
}
