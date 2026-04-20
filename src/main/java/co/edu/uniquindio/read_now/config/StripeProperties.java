package co.edu.uniquindio.read_now.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.stripe")
public class StripeProperties {

    /** sk_test_... o sk_live_... (solo servidor; nunca en el front). */
    private String secretKey = "";

    /** pk_test_... para Stripe.js / Checkout en el front. */
    private String publishableKey = "";

    /** Secreto del endpoint de webhook (whsec_...). */
    private String webhookSecret = "";

    /** Ruta bajo app.frontend.url tras pago OK (Stripe sustituye {CHECKOUT_SESSION_ID}). */
    private String successPath = "/suscripcion/pago-exitoso";

    /** Ruta bajo app.frontend.url si el usuario cancela en Checkout. */
    private String cancelPath = "/suscripcion/pago-cancelado";

    /** IDs de Price en Stripe (modo test) — crealos en Dashboard → Productos → Precios recurrentes en COP. */
    private String priceMensual = "";
    private String priceSemestral = "";
    private String priceAnual = "";
}
