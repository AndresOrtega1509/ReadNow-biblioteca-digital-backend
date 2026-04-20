package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.SuscripcionCheckoutCodigoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.dto.response.StripeCheckoutSessionResponseDTO;
import co.edu.uniquindio.read_now.dto.response.StripePortalSessionResponseDTO;
import co.edu.uniquindio.read_now.dto.response.SuscripcionPlanesCatalogoResponseDTO;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.ISuscripcionPlanesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suscripcion")
@RequiredArgsConstructor
@Tag(name = "Suscripción", description = "Planes, prueba gratuita y checkout Stripe")
public class SuscripcionPlanesController {

    private final ISuscripcionPlanesService suscripcionPlanesService;
    private final JwtUtil jwtUtil;

    @GetMapping("/planes")
    @Operation(summary = "Catálogo de planes", description = "Planes de pago + si puede activar la prueba gratuita de 15 días (una sola vez)")
    public ResponseEntity<SuscripcionPlanesCatalogoResponseDTO> planes(HttpServletRequest request) {
        Long usuarioId = jwtUtil.getUsuarioIdFromToken(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(suscripcionPlanesService.obtenerCatalogoPlanes(usuarioId));
    }

    @PostMapping("/prueba-gratuita")
    @Operation(summary = "Activar prueba gratuita", description = "15 días de acceso; solo una vez por usuario lector")
    public ResponseEntity<MensajeResponseDTO> pruebaGratuita(HttpServletRequest request) {
        Long usuarioId = jwtUtil.getUsuarioIdFromToken(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(suscripcionPlanesService.activarPruebaGratuita(usuarioId));
    }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout Stripe", description = "Crea sesión de pago para MENSUAL, SEMESTRAL o ANUAL")
    public ResponseEntity<StripeCheckoutSessionResponseDTO> checkout(
            HttpServletRequest request,
            @Valid @RequestBody SuscripcionCheckoutCodigoRequestDTO body) {
        Long usuarioId = jwtUtil.getUsuarioIdFromToken(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(suscripcionPlanesService.crearCheckoutPorPlan(usuarioId, body.codigoPlan()));
    }

    @PostMapping("/portal-gestion")
    @Operation(summary = "Portal Stripe", description = "Renovar / cancelar suscripción (Stripe Customer Portal)")
    public ResponseEntity<StripePortalSessionResponseDTO> portal(HttpServletRequest request) {
        Long usuarioId = jwtUtil.getUsuarioIdFromToken(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(suscripcionPlanesService.crearSesionPortalGestion(usuarioId));
    }
}
