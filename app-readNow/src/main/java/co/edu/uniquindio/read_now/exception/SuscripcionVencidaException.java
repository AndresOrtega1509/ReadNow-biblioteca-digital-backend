package co.edu.uniquindio.read_now.exception;

public class SuscripcionVencidaException extends RuntimeException {

    public SuscripcionVencidaException() {
        super("Tu suscripción ha vencido. Renueva para acceder al catálogo y leer recursos.");
    }
}
