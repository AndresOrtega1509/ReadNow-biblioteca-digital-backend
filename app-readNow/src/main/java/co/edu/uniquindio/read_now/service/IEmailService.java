package co.edu.uniquindio.read_now.service;

public interface IEmailService {

    void enviarCodigoVerificacion(String email, String nombre, String codigo);

    void enviarEnlaceRecuperacion(String email, String nombre, String urlEnlace);

    void enviarTokenRecuperacion(String email, String nombre, String token);

    void enviarCorreoInactividad(String email, String nombre);
}
