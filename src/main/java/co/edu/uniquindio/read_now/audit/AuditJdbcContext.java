package co.edu.uniquindio.read_now.audit;

/**
 * Usuario de aplicación asociado al hilo actual, para variables de sesión MySQL ({@code @audit_usuario}).
 */
public final class AuditJdbcContext {

    private static final ThreadLocal<String> USUARIO = new ThreadLocal<>();

    private AuditJdbcContext() {}

    public static void setUsuario(String emailOUsuario) {
        if (emailOUsuario == null || emailOUsuario.isBlank()) {
            USUARIO.remove();
        } else {
            USUARIO.set(emailOUsuario.trim());
        }
    }

    public static String getUsuario() {
        return USUARIO.get();
    }

    public static void clear() {
        USUARIO.remove();
    }
}
