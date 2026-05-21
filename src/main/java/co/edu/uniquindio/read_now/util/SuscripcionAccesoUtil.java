package co.edu.uniquindio.read_now.util;

import co.edu.uniquindio.read_now.model.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reglas compartidas de vigencia de acceso al catálogo (planes de pago o prueba gratuita).
 */
public final class SuscripcionAccesoUtil {

    private SuscripcionAccesoUtil() {
    }

    public static boolean esSuscripcionActiva(Usuario usuario) {
        LocalDateTime ahora = LocalDateTime.now();
        if (usuario.getFinSuscripcionAt() != null) {
            return ahora.isBefore(usuario.getFinSuscripcionAt());
        }
        return usuario.getFinSuscripcion() != null
                && !usuario.getFinSuscripcion().isBefore(LocalDate.now());
    }

    /**
     * True si el lector activó prueba, tuvo plan de pago o quedó registro de periodo (vigente o vencido).
     */
    public static boolean haTenidoSuscripcion(Usuario usuario) {
        if (Boolean.TRUE.equals(usuario.getPruebaGratuitaUsada())) {
            return true;
        }
        if (usuario.getInicioSuscripcion() != null) {
            return true;
        }
        if (usuario.getFinSuscripcion() != null) {
            return true;
        }
        if (usuario.getFinSuscripcionAt() != null) {
            return true;
        }
        if (usuario.getSuscripcion() != null) {
            return true;
        }
        String stripeSub = usuario.getStripeSubscriptionId();
        return stripeSub != null && !stripeSub.isBlank();
    }
}
