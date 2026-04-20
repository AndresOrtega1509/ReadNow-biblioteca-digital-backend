package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.config.StripeClientProvider;
import co.edu.uniquindio.read_now.model.Suscripcion;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.ISuscripcionRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import com.stripe.StripeClient;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Aplica eventos de Stripe al modelo de suscripción local (renovación, alta, cancelación).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuscripcionStripeWebhookService {

    private static final ZoneId ZONA_CO = ZoneId.of("America/Bogota");

    private final StripeClientProvider stripeClientProvider;
    private final IUsuarioRepository usuarioRepository;
    private final ISuscripcionRepository suscripcionRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void procesarEvento(Event event, String rawJsonPayload) {
        switch (event.getType()) {
            case "checkout.session.completed" -> onCheckoutSessionCompleted(event, rawJsonPayload);
            case "customer.subscription.updated" -> onSubscriptionUpdated(event, rawJsonPayload);
            case "customer.subscription.deleted" -> onSubscriptionDeleted(event, rawJsonPayload);
            default -> log.debug("Stripe webhook ignorado: {}", event.getType());
        }
    }

    private void onCheckoutSessionCompleted(Event event, String rawJsonPayload) {
        StripeClient client = stripeClientProvider.client().orElse(null);
        if (client == null) {
            return;
        }
        Session session = resolverSesionCheckout(client, event, rawJsonPayload);
        if (session == null) {
            log.warn("checkout.session.completed: no se pudo obtener la sesión");
            return;
        }
        if (!"subscription".equalsIgnoreCase(String.valueOf(session.getMode()))) {
            return;
        }
        String subId = session.getSubscription();
        if (subId == null || subId.isBlank()) {
            log.warn("checkout sin subscription id");
            return;
        }
        try {
            Subscription stripeSubscription = client.subscriptions().retrieve(subId);
            aplicarSuscripcionDesdeStripe(stripeSubscription, session.getClientReferenceId(), session.getCustomer());
        } catch (Exception e) {
            log.error("Error procesando checkout.session.completed", e);
            throw new RuntimeException(e);
        }
    }

    private String idObjetoDesdePayload(String rawJsonPayload) {
        try {
            JsonNode n = objectMapper.readTree(rawJsonPayload);
            JsonNode id = n.path("data").path("object").path("id");
            return id.isMissingNode() || id.isNull() ? null : id.asText();
        } catch (Exception e) {
            log.warn("No se pudo leer id del payload webhook: {}", e.getMessage());
            return null;
        }
    }

    private Session resolverSesionCheckout(StripeClient client, Event event, String rawJsonPayload) {
        StripeObject obj = event.getDataObjectDeserializer().getObject().orElse(null);
        if (obj instanceof Session s) {
            return s;
        }
        String sid = idObjetoDesdePayload(rawJsonPayload);
        if (sid == null || sid.isBlank()) {
            return null;
        }
        try {
            return client.checkout().sessions().retrieve(
                    sid,
                    com.stripe.param.checkout.SessionRetrieveParams.builder()
                            .addExpand("subscription")
                            .build(),
                    null);
        } catch (Exception e) {
            log.error("No se pudo recuperar la sesión de checkout {}", sid, e);
            return null;
        }
    }

    private void onSubscriptionUpdated(Event event, String rawJsonPayload) {
        StripeClient client = stripeClientProvider.client().orElse(null);
        if (client == null) {
            return;
        }
        StripeObject obj = event.getDataObjectDeserializer().getObject().orElse(null);
        String subId = obj instanceof Subscription s ? s.getId() : idObjetoDesdePayload(rawJsonPayload);
        if (subId == null || subId.isBlank()) {
            return;
        }
        try {
            Subscription stripeSubscription = client.subscriptions().retrieve(subId);
            Usuario usuario = usuarioRepository.findByStripeSubscriptionId(stripeSubscription.getId()).orElse(null);
            if (usuario == null) {
                log.warn("subscription.updated sin usuario local para sub {}", subId);
                return;
            }
            aplicarPeriodo(usuario, stripeSubscription);
            usuarioRepository.save(usuario);
        } catch (Exception e) {
            log.error("Error en subscription.updated", e);
            throw new RuntimeException(e);
        }
    }

    private void onSubscriptionDeleted(Event event, String rawJsonPayload) {
        StripeObject obj = event.getDataObjectDeserializer().getObject().orElse(null);
        String subId = obj instanceof Subscription s ? s.getId() : idObjetoDesdePayload(rawJsonPayload);
        if (subId == null || subId.isBlank()) {
            return;
        }
        usuarioRepository.findByStripeSubscriptionId(subId).ifPresent(usuario -> {
            usuario.setStripeSubscriptionId(null);
            LocalDateTime ahora = LocalDateTime.now();
            usuario.setFinSuscripcionAt(ahora);
            usuario.setFinSuscripcion(ahora.toLocalDate());
            usuarioRepository.save(usuario);
            log.info("Suscripción Stripe eliminada para usuario {}", usuario.getUsuarioId());
        });
    }

    private void aplicarSuscripcionDesdeStripe(Subscription stripeSubscription, String clientReferenceId, String customerId) {
        Long usuarioId = parseUsuarioId(clientReferenceId, stripeSubscription);
        if (usuarioId == null) {
            log.warn("No se pudo resolver usuario_id desde checkout/subscription");
            return;
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + usuarioId));

        Suscripcion plan = resolverPlan(stripeSubscription);
        usuario.setSuscripcion(plan);
        usuario.setStripeCustomerId(customerId);
        usuario.setStripeSubscriptionId(stripeSubscription.getId());
        aplicarPeriodo(usuario, stripeSubscription);
        usuario.setSuscripcionVencidaNotificada(false);
        usuarioRepository.save(usuario);
        log.info("Suscripción activada vía Stripe para usuario {}", usuarioId);
    }

    private Long parseUsuarioId(String clientReferenceId, Subscription subscription) {
        if (clientReferenceId != null && !clientReferenceId.isBlank()) {
            try {
                return Long.parseLong(clientReferenceId.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        String meta = subscription.getMetadata() != null ? subscription.getMetadata().get("usuario_id") : null;
        if (meta != null) {
            try {
                return Long.parseLong(meta.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private Suscripcion resolverPlan(Subscription stripeSubscription) {
        String priceId = stripeSubscription.getItems().getData().isEmpty()
                ? null
                : stripeSubscription.getItems().getData().get(0).getPrice().getId();
        if (priceId != null) {
            var porPrice = suscripcionRepository.findByStripePriceId(priceId);
            if (porPrice.isPresent()) {
                return porPrice.get();
            }
        }
        String codigo = stripeSubscription.getMetadata() != null ? stripeSubscription.getMetadata().get("plan_codigo") : null;
        if (codigo != null) {
            return suscripcionRepository.findByCodigoPlan(codigo)
                    .orElseThrow(() -> new IllegalStateException("Plan no encontrado: " + codigo));
        }
        throw new IllegalStateException("No se pudo mapear el plan de la suscripción Stripe");
    }

    private void aplicarPeriodo(Usuario usuario, Subscription stripeSubscription) {
        long start = stripeSubscription.getCurrentPeriodStart();
        long end = stripeSubscription.getCurrentPeriodEnd();
        LocalDateTime inicio = LocalDateTime.ofInstant(Instant.ofEpochSecond(start), ZONA_CO);
        LocalDateTime fin = LocalDateTime.ofInstant(Instant.ofEpochSecond(end), ZONA_CO);
        usuario.setInicioSuscripcion(inicio.toLocalDate());
        usuario.setFinSuscripcion(fin.toLocalDate());
        usuario.setFinSuscripcionAt(fin);
    }
}
