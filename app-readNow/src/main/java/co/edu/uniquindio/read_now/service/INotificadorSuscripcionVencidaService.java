package co.edu.uniquindio.read_now.service;

public interface INotificadorSuscripcionVencidaService {

    /**
     * Si el usuario tiene suscripción vencida y no ha sido notificado, envía el correo
     * y marca como notificado. Se ejecuta de forma asíncrona.
     */
    void notificarSiCorresponde(Long usuarioId);
}
