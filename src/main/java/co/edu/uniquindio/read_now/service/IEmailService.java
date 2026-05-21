package co.edu.uniquindio.read_now.service;

public interface IEmailService {

    void enviarCodigoVerificacion(String email, String nombre, String codigo);

    void enviarEnlaceRecuperacion(String email, String nombre, String urlEnlace);

    void enviarTokenRecuperacion(String email, String nombre, String token);

    void enviarCorreoInactividad(String email, String nombre, int diasInactividad);

    /** Notifica al lector que su suscripción o prueba gratuita ha vencido. */
    void enviarCorreoSuscripcionVencida(String email, String nombre);

    /** Recordatorio: la suscripción vence en X días (5 o 1). */
    void enviarRecordatorioSuscripcionPorVencer(String email, String nombre, int diasRestantes);

    void enviarPqrRecibidaLector(String email, String nombre, Long pqrId, String asunto);

    void enviarPqrNuevaAdmin(String email, String nombreAdmin, Long pqrId, String asunto, String tipo,
                             String lectorNombre, String lectorEmail, String descripcion);

    void enviarPqrCambioEstadoLector(String email, String nombre, Long pqrId, String asunto,
                                     String estadoAnterior, String estadoNuevo, String mensajeAdmin);

    void enviarPqrRespuestaAdminLector(String email, String nombre, Long pqrId, String asunto, String mensaje);

    void enviarPqrMensajeLectorAdmin(String email, String nombreAdmin, Long pqrId, String asunto,
                                     String lectorNombre, String mensaje);
}
