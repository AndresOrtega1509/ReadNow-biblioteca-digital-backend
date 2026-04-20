package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.config.StripeClientProvider;
import co.edu.uniquindio.read_now.config.StripeProperties;
import co.edu.uniquindio.read_now.dto.response.StripeCheckoutSessionResponseDTO;
import co.edu.uniquindio.read_now.dto.response.StripePortalSessionResponseDTO;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.ISuscripcionRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private final StripeClientProvider stripeClientProvider;
    private final StripeProperties stripeProperties;
    private final IUsuarioRepository usuarioRepository;
    private final ISuscripcionRepository suscripcionRepository;

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

    private StripeClient client() {
        return stripeClientProvider.client()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Pagos con Stripe no están configurados en el servidor."));
    }

    /** Checkout por código de plan (MENSUAL, SEMESTRAL, ANUAL). */
    public StripeCheckoutSessionResponseDTO crearCheckoutSuscripcion(String emailUsuario, String codigoPlan) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var plan = suscripcionRepository.findByCodigoPlan(codigoPlan)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plan no encontrado: " + codigoPlan));

        String priceId = resolverStripePriceId(plan);
        if (!esStripePriceIdValido(priceId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Falta el Price ID de Stripe (price_...) para el plan " + codigoPlan
                            + ". Crea precios recurrentes en COP en el Dashboard y configura app.stripe.price-mensual, "
                            + "price-semestral y price-anual con esos IDs (no uses el monto en pesos).");
        }

        return crearSesionCheckout(usuario, priceId, codigoPlan);
    }

    /** Checkout directo con un price_... (útil en pruebas). */
    public StripeCheckoutSessionResponseDTO crearCheckoutConPriceId(String emailUsuario, String priceId) {
        if (!esStripePriceIdValido(priceId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El priceId debe ser un ID de Stripe que empiece por price_...");
        }
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return crearSesionCheckout(usuario, priceId.trim(), null);
    }

    private String resolverStripePriceId(co.edu.uniquindio.read_now.model.Suscripcion plan) {
        if (esStripePriceIdValido(plan.getStripePriceId())) {
            return plan.getStripePriceId().trim();
        }
        if (plan.getCodigoPlan() == null) {
            return "";
        }
        return switch (plan.getCodigoPlan()) {
            case co.edu.uniquindio.read_now.model.PlanSuscripcionCodigo.MENSUAL -> nz(stripeProperties.getPriceMensual());
            case co.edu.uniquindio.read_now.model.PlanSuscripcionCodigo.SEMESTRAL -> nz(stripeProperties.getPriceSemestral());
            case co.edu.uniquindio.read_now.model.PlanSuscripcionCodigo.ANUAL -> nz(stripeProperties.getPriceAnual());
            default -> "";
        };
    }

    private static boolean esStripePriceIdValido(String id) {
        return id != null && !id.isBlank() && id.trim().startsWith("price_");
    }

    private static String nz(String s) {
        return s == null ? "" : s.trim();
    }

    private StripeCheckoutSessionResponseDTO crearSesionCheckout(Usuario usuario, String priceId, String planCodigo) {
        String base = frontendBaseUrl.endsWith("/")
                ? frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1)
                : frontendBaseUrl;
        String success = base + stripeProperties.getSuccessPath() + "?session_id={CHECKOUT_SESSION_ID}";
        String cancel = base + stripeProperties.getCancelPath();

        SessionCreateParams.Builder b = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(success)
                .setCancelUrl(cancel)
                .setCustomerEmail(usuario.getEmail())
                .setClientReferenceId(String.valueOf(usuario.getUsuarioId()))
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(priceId)
                                .setQuantity(1L)
                                .build())
                .putMetadata("usuario_id", String.valueOf(usuario.getUsuarioId()));

        SessionCreateParams.SubscriptionData.Builder subData = SessionCreateParams.SubscriptionData.builder()
                .putMetadata("usuario_id", String.valueOf(usuario.getUsuarioId()));
        if (planCodigo != null) {
            subData.putMetadata("plan_codigo", planCodigo);
        }
        b.setSubscriptionData(subData.build());

        try {
            com.stripe.model.checkout.Session session = client().checkout().sessions().create(b.build());
            return new StripeCheckoutSessionResponseDTO(session.getId(), session.getUrl());
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo crear la sesión de pago: " + e.getMessage());
        }
    }

    public StripePortalSessionResponseDTO crearBillingPortalSession(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        String customerId = usuario.getStripeCustomerId();
        if (customerId == null || customerId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sin cliente Stripe asociado.");
        }
        String base = frontendBaseUrl.endsWith("/")
                ? frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1)
                : frontendBaseUrl;
        String returnUrl = base + "/suscripcion/planes";

        com.stripe.param.billingportal.SessionCreateParams params = com.stripe.param.billingportal.SessionCreateParams.builder()
                .setCustomer(customerId)
                .setReturnUrl(returnUrl)
                .build();
        try {
            com.stripe.model.billingportal.Session portal = client().billingPortal().sessions().create(params);
            return new StripePortalSessionResponseDTO(portal.getUrl());
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "No se pudo abrir el portal de facturación: " + e.getMessage());
        }
    }
}
