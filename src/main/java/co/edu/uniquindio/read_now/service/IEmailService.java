package co.edu.uniquindio.read_now.service;

public interface IEmailService {

    void enviarCodigoVerificacion(String email, String nombre, String codigo);

    void enviarEnlaceRecuperacion(String email, String nombre, String urlEnlace);

    void enviarTokenRecuperacion(String email, String nombre, String token);

    void enviarCorreoInactividad(String email, String nombre);

    /** Notifica al lector que su suscripción o prueba gratuita ha vencido. */
    void enviarCorreoSuscripcionVencida(String email, String nombre);

    /** Recordatorio: la suscripción vence en X días (5 o 1). */
    void enviarRecordatorioSuscripcionPorVencer(String email, String nombre, int diasRestantes);
}
