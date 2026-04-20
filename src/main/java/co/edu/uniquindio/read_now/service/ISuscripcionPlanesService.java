package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.dto.response.StripeCheckoutSessionResponseDTO;
import co.edu.uniquindio.read_now.dto.response.StripePortalSessionResponseDTO;
import co.edu.uniquindio.read_now.dto.response.SuscripcionPlanesCatalogoResponseDTO;

public interface ISuscripcionPlanesService {

    SuscripcionPlanesCatalogoResponseDTO obtenerCatalogoPlanes(Long usuarioId);

    MensajeResponseDTO activarPruebaGratuita(Long usuarioId);

    StripeCheckoutSessionResponseDTO crearCheckoutPorPlan(Long usuarioId, String codigoPlan);

    StripePortalSessionResponseDTO crearSesionPortalGestion(Long usuarioId);
}
