package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.dto.response.StripeCheckoutSessionResponseDTO;
import co.edu.uniquindio.read_now.dto.response.StripePortalSessionResponseDTO;
import co.edu.uniquindio.read_now.dto.response.SuscripcionPlanResponseDTO;
import co.edu.uniquindio.read_now.dto.response.SuscripcionPlanesCatalogoResponseDTO;
import co.edu.uniquindio.read_now.config.StripeProperties;
import co.edu.uniquindio.read_now.model.PlanSuscripcionCodigo;
import co.edu.uniquindio.read_now.model.Suscripcion;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.ISuscripcionRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.ISuscripcionPlanesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuscripcionPlanesServiceImpl implements ISuscripcionPlanesService {

    private final ISuscripcionRepository suscripcionRepository;
    private final IUsuarioRepository usuarioRepository;
    private final StripePaymentService stripePaymentService;
    private final StripeProperties stripeProperties;

    @Value("${app.trial.days}")
    private int diasPruebaGratuita;

    @Override
    public SuscripcionPlanesCatalogoResponseDTO obtenerCatalogoPlanes(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<SuscripcionPlanResponseDTO> planes = suscripcionRepository.findAll().stream()
                .filter(s -> s.getCodigoPlan() != null
                        && List.of(PlanSuscripcionCodigo.MENSUAL, PlanSuscripcionCodigo.SEMESTRAL, PlanSuscripcionCodigo.ANUAL)
                        .contains(s.getCodigoPlan()))
                .sorted(Comparator.comparing(Suscripcion::getDuracion))
                .map(s -> new SuscripcionPlanResponseDTO(
                        s.getCodigoPlan(),
                        s.getNombre(),
                        s.getDescripcion(),
                        s.getPrecio(),
                        s.getDuracion(),
                        stripeConfiguradoParaPlan(s)))
                .toList();

        boolean puedePrueba = puedeActivarPruebaGratuita(usuario);
        boolean portal = usuario.getStripeCustomerId() != null && !usuario.getStripeCustomerId().isBlank();
        return new SuscripcionPlanesCatalogoResponseDTO(planes, puedePrueba, diasPruebaGratuita, portal);
    }

    private boolean puedeActivarPruebaGratuita(Usuario usuario) {
        if (!"LECTOR".equals(usuario.getRol().getNombre())) {
            return false;
        }
        if (Boolean.TRUE.equals(usuario.getPruebaGratuitaUsada())) {
            return false;
        }
        return !esSuscripcionActiva(usuario);
    }

    private boolean esSuscripcionActiva(Usuario usuario) {
        LocalDateTime ahora = LocalDateTime.now();
        if (usuario.getFinSuscripcionAt() != null) {
            return ahora.isBefore(usuario.getFinSuscripcionAt());
        }
        return usuario.getFinSuscripcion() != null
                && !usuario.getFinSuscripcion().isBefore(LocalDate.now());
    }

    @Override
    @Transactional
    public MensajeResponseDTO activarPruebaGratuita(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!"LECTOR".equals(usuario.getRol().getNombre())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo lectores pueden activar la prueba gratuita.");
        }
        if (Boolean.TRUE.equals(usuario.getPruebaGratuitaUsada())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya usaste la prueba gratuita.");
        }
        if (esSuscripcionActiva(usuario)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya tienes una suscripción activa.");
        }

        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusDays(diasPruebaGratuita);
        usuario.setInicioSuscripcion(inicio.toLocalDate());
        usuario.setFinSuscripcion(fin.toLocalDate());
        usuario.setFinSuscripcionAt(fin);
        usuario.setPruebaGratuitaUsada(true);
        usuario.setSuscripcion(null);
        usuario.setSuscripcionVencidaNotificada(false);
        usuarioRepository.save(usuario);
        return new MensajeResponseDTO(true, "Prueba gratuita activada por " + diasPruebaGratuita + " días.");
    }

    @Override
    public StripeCheckoutSessionResponseDTO crearCheckoutPorPlan(Long usuarioId, String codigoPlan) {
        if (!List.of(PlanSuscripcionCodigo.MENSUAL, PlanSuscripcionCodigo.SEMESTRAL, PlanSuscripcionCodigo.ANUAL)
                .contains(codigoPlan)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "codigoPlan no válido.");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!"LECTOR".equals(usuario.getRol().getNombre())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo lectores pueden suscribirse.");
        }
        return stripePaymentService.crearCheckoutSuscripcion(usuario.getEmail(), codigoPlan);
    }

    @Override
    public StripePortalSessionResponseDTO crearSesionPortalGestion(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (usuario.getStripeCustomerId() == null || usuario.getStripeCustomerId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No hay una suscripción de pago asociada. Contrata un plan primero.");
        }
        return stripePaymentService.crearBillingPortalSession(usuario.getEmail());
    }

    /**
     * Misma idea que {@link StripePaymentService}: precio válido en BD o en {@code app.stripe.price-*}.
     * Así el catálogo no muestra “sin Stripe” si solo falló la sincronización a la tabla.
     */
    private boolean stripeConfiguradoParaPlan(Suscripcion s) {
        if (stripePriceIdConfigurado(s.getStripePriceId())) {
            return true;
        }
        String codigo = s.getCodigoPlan();
        if (codigo == null) {
            return false;
        }
        String fromProps = switch (codigo) {
            case PlanSuscripcionCodigo.MENSUAL -> nz(stripeProperties.getPriceMensual());
            case PlanSuscripcionCodigo.SEMESTRAL -> nz(stripeProperties.getPriceSemestral());
            case PlanSuscripcionCodigo.ANUAL -> nz(stripeProperties.getPriceAnual());
            default -> "";
        };
        return stripePriceIdConfigurado(fromProps);
    }

    private static String nz(String v) {
        return v == null ? "" : v.trim();
    }

    private static boolean stripePriceIdConfigurado(String id) {
        return id != null && !id.isBlank() && id.trim().startsWith("price_");
    }
}
