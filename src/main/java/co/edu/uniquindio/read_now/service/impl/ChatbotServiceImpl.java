package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.ChatMensajeRequestDTO;
import co.edu.uniquindio.read_now.dto.response.SuscripcionPlanResponseDTO;
import co.edu.uniquindio.read_now.dto.response.SuscripcionPlanesCatalogoResponseDTO;
import co.edu.uniquindio.read_now.dto.response.UsuarioResponseDTO;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.service.IChatbotService;
import co.edu.uniquindio.read_now.service.ISuscripcionPlanesService;
import co.edu.uniquindio.read_now.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements IChatbotService {

    private final IUsuarioService usuarioService;
    private final ISuscripcionPlanesService suscripcionPlanesService;
    private final IRecursoRepository recursoRepository;

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("es"));
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es"));

    @Override
    public String responder(Long usuarioId, ChatMensajeRequestDTO request) {
        UsuarioResponseDTO perfil = usuarioService.obtenerPerfil(usuarioId);
        SuscripcionPlanesCatalogoResponseDTO catalogo = suscripcionPlanesService.obtenerCatalogoPlanes(usuarioId);
        long totalActivos = recursoRepository.countByActivo("S");
        String pagina = request.paginaContexto() != null ? request.paginaContexto().trim() : "";
        String nombre = perfil.nombre() != null ? perfil.nombre().trim() : "";
        String m = normalizar(request.mensaje());

        return construirRespuesta(m, nombre, totalActivos, pagina, perfil, catalogo);
    }

    private static String normalizar(String raw) {
        if (raw == null) {
            return "";
        }
        String lower = raw.toLowerCase(Locale.ROOT).trim();
        return java.text.Normalizer.normalize(lower, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }

    private String construirRespuesta(
            String m,
            String nombre,
            long totalActivos,
            String pagina,
            UsuarioResponseDTO perfil,
            SuscripcionPlanesCatalogoResponseDTO catalogo) {
        String saludo = !nombre.isEmpty() ? "¡Hola, " + nombre + "! " : "¡Hola! ";

        if (contieneAlguno(m, "hola", "buenos", "buenas", "hey", "buen dia", "buena tarde", "buena noche")) {
            return saludo + "Soy el asistente de ReadNow. Hay " + totalActivos + " recursos activos en el catálogo. "
                    + "Puedo contarte sobre tu suscripción y fechas, los planes disponibles, el catálogo, favoritos o tu perfil. "
                    + "¿Qué necesitas?";
        }

        if (contieneAlguno(m, "que puedes", "que sabes", "menu", "opciones", "temas", "que temas", "que preguntar")) {
            return textoMenu(totalActivos);
        }

        if (intentaPlanesDisponibles(m)) {
            return textoPlanesLista(catalogo);
        }

        if (contieneAlguno(m, "prueba gratuita", "probar gratis", "dias gratis", "gratis primero")) {
            return textoPruebaGratuita(catalogo, perfil);
        }

        if (contieneAlguno(m, "portal", "stripe", "cancelar suscripcion", "dar de baja", "anular suscripcion")
                || (m.contains("renovar") && m.contains("pago"))) {
            return textoPortalStripe(catalogo, perfil);
        }

        if (intentaDetalleSuscripcion(m)) {
            return textoEstadoSuscripcion(perfil, catalogo);
        }

        if (contieneAlguno(m, "ayuda", "no entiendo", "como funciona")) {
            return "Te guío sobre ReadNow: catálogo y lectura, favoritos, historial, perfil, y suscripción o planes. "
                    + "Pregunta, por ejemplo: «¿Cuándo vence mi suscripción?», «¿Qué planes hay?» o «¿Puedo usar la prueba gratuita?». "
                    + "Ahora hay " + totalActivos + " recursos en el catálogo.";
        }

        if (contieneAlguno(m, "catalogo", "libro", "buscar", "recurso", "leer", "material")) {
            return "En el Catálogo exploras los recursos activos (" + totalActivos + " ahora). "
                    + "Usa la búsqueda del encabezado. Al abrir un recurso verás detalle, calificaciones, reseñas y el visor para leer.";
        }

        if (m.contains("favorito")) {
            return "En Favoritos guardas recursos para después. Desde el detalle puedes añadirlos o quitarlos.";
        }

        if (m.contains("historial")) {
            return "El Historial guarda tu actividad de lectura para retomar lo que abriste.";
        }

        if (m.contains("suscripcion") || m.contains("plan")) {
            return textoEstadoSuscripcion(perfil, catalogo) + "\n\n"
                    + "Si quieres ver precios y duraciones, pregunta por los planes disponibles.";
        }

        if (contieneAlguno(m, "perfil", "cuenta", "password", "contrasena", "clave")) {
            return "En Perfil ves tus datos, el estado de la suscripción y opciones como cambio de contraseña o verificación en dos pasos si aplica.";
        }

        if (contieneAlguno(m, "gracias", "muchas gracias")) {
            return "¡Con gusto! Si necesitas algo más sobre ReadNow, escríbeme.";
        }

        if (!pagina.isEmpty()) {
            return saludo + "Estás en " + pagina + ". "
                    + "Prueba preguntar por tu suscripción, los planes disponibles, el catálogo o el perfil, o reformula la pregunta.";
        }

        return saludo + "Puedo ayudarte con tu suscripción (vigencia y plan), planes y precios, prueba gratuita, catálogo ("
                + totalActivos + " recursos), favoritos, historial o perfil. ¿Sobre qué quieres saber?";
    }

    private static boolean intentaPlanesDisponibles(String m) {
        if (m.contains("planes disponibles") || m.contains("plan disponible") || m.contains("catalogo de planes")
                || m.contains("que planes") || m.contains("cuales planes") || m.contains("cuantos planes")
                || m.contains("planes de pago") || m.contains("tipos de plan")) {
            return true;
        }
        if (m.contains("precio") || m.contains("precios") || m.contains("cuesta") || m.contains("costo")
                || m.contains("tarifa") || m.contains("membresia") || m.contains("contratar") || m.contains("suscribirme")
                || m.contains("pagar el plan") || m.contains("pagar plan")) {
            return true;
        }
        return m.contains("cuanto") && m.contains("cuesta");
    }

    private static boolean intentaDetalleSuscripcion(String m) {
        if (m.contains("mi plan") || m.contains("plan actual") || m.contains("que plan tengo") || m.contains("cual plan")) {
            return true;
        }
        if (m.contains("mi suscripcion") || m.contains("estado de mi") || m.contains("tengo acceso")) {
            return true;
        }
        if (m.contains("vence") || m.contains("vencimiento") || m.contains("vencio") || m.contains("caduca")
                || m.contains("expira") || m.contains("expiracion")) {
            return true;
        }
        if (m.contains("vigencia") || m.contains("hasta cuando") || m.contains("fecha fin") || m.contains("cuando termina")) {
            return true;
        }
        return m.contains("renovar") && (m.contains("suscripcion") || m.contains("acceso") || m.contains("plan"));
    }

    private static boolean contieneAlguno(String m, String... tokens) {
        for (String t : tokens) {
            if (m.contains(t)) {
                return true;
            }
        }
        return false;
    }

    private static String textoMenu(long totalActivos) {
        return "Puedes preguntar, por ejemplo:\n"
                + "• «¿Cuándo vence mi suscripción?» o «¿Qué plan tengo?»\n"
                + "• «¿Qué planes hay?» o «¿Cuánto cuesta el plan mensual?»\n"
                + "• «¿Puedo activar la prueba gratuita?»\n"
                + "• «¿Cómo renovo o cancelo en Stripe?»\n"
                + "• Baja de la plataforma: en Perfil, sección «Baja de la plataforma» (desactiva la cuenta sin borrar datos)\n"
                + "• Catálogo, favoritos, historial o perfil\n\n"
                + "Hay " + totalActivos + " recursos activos en el catálogo.";
    }

    private static String textoPlanesLista(SuscripcionPlanesCatalogoResponseDTO c) {
        if (c.planesPago().isEmpty()) {
            return "En este momento no hay planes de pago configurados en el sistema. Un administrador debe cargarlos.";
        }
        NumberFormat money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
        StringBuilder sb = new StringBuilder("Planes disponibles para contratar:\n");
        for (SuscripcionPlanResponseDTO pl : c.planesPago()) {
            String meses = pl.duracionMeses() == 1 ? "1 mes" : pl.duracionMeses() + " meses";
            sb.append("• ").append(pl.nombre()).append(" — ").append(money.format(pl.precioCop()));
            sb.append(" (").append(meses).append(")\n");
            if (pl.descripcion() != null && !pl.descripcion().isBlank()) {
                sb.append("  ").append(pl.descripcion().trim()).append("\n");
            }
            if (!pl.stripeConfigurado()) {
                sb.append("  (Falta enlazar el precio en Stripe para poder pagar este plan en la app.)\n");
            }
        }
        sb.append("\nContrátalos desde la sección Planes o desde Perfil, según veas en la aplicación.");
        return sb.toString().trim();
    }

    private static String textoPruebaGratuita(SuscripcionPlanesCatalogoResponseDTO c, UsuarioResponseDTO p) {
        if (!"LECTOR".equals(p.rol())) {
            return "La prueba gratuita aplica a cuentas con rol lector. Tu rol actual es «" + p.rol() + "». "
                    + "Si necesitas acceso como lector, habla con un administrador.";
        }
        if (c.puedeActivarPruebaGratuita()) {
            return "Puedes activar una prueba gratuita de " + c.diasPruebaGratuita()
                    + " días desde Perfil o la pantalla de Planes, si aún no has usado este beneficio.";
        }
        if (p.suscripcionActiva()) {
            return "Ya tienes acceso activo. La prueba gratuita es única y solo se ofrece cuando no hay un periodo vigente.";
        }
        return "La prueba gratuita es de una sola vez por cuenta lector; en tu caso ya no está disponible. "
                + "Puedes revisar los planes de pago preguntando por «planes disponibles».";
    }

    private static String textoPortalStripe(SuscripcionPlanesCatalogoResponseDTO c, UsuarioResponseDTO p) {
        if ("ADMIN".equals(p.rol())) {
            return "Como administrador, la gestión de cobros de los lectores no usa tu cuenta personal de la misma forma. "
                    + "Los lectores renuevan o cancelan desde Perfil y el portal de Stripe cuando ya tienen un pago registrado.";
        }
        if (c.puedeGestionarEnStripe()) {
            return "En Perfil puedes abrir el portal de cliente de Stripe para actualizar el método de pago, "
                    + "ver facturas o cancelar la suscripción recurrente.";
        }
        return "Si aún no completaste un pago, elige un plan en la sección Planes y finaliza el checkout. "
                + "Después de eso podrás usar el portal de Stripe desde Perfil cuando corresponda.";
    }

    private static String textoEstadoSuscripcion(UsuarioResponseDTO p, SuscripcionPlanesCatalogoResponseDTO c) {
        StringBuilder sb = new StringBuilder();
        if ("ADMIN".equals(p.rol())) {
            sb.append("Eres administrador: tu acceso no depende de un plan de lector. ");
        }
        if (p.suscripcionActiva()) {
            sb.append("Tu acceso está activo. ");
            if (p.nombrePlanSuscripcion() != null) {
                sb.append("Plan o tipo de acceso: ").append(p.nombrePlanSuscripcion()).append(". ");
            }
            String fin = formatearFinAcceso(p);
            if (fin != null) {
                sb.append("La vigencia actual termina el ").append(fin).append(". ");
            }
        } else if (!p.haTenidoSuscripcion()) {
            sb.append("Aún no has activado la prueba gratuita ni un plan de pago. ");
        } else {
            sb.append("Tu periodo de acceso como lector ha vencido. ");
            String fin = formatearFinAcceso(p);
            if (fin != null) {
                sb.append("El fin del último periodo registrado fue el ").append(fin).append(". ");
            }
        }
        if (p.inicioSuscripcion() != null && p.suscripcionActiva()) {
            sb.append("Inicio del periodo actual: ").append(FECHA.format(p.inicioSuscripcion())).append(". ");
        }
        if (!"ADMIN".equals(p.rol())) {
            if (c.puedeActivarPruebaGratuita()) {
                sb.append("Puedes activar la prueba gratuita (").append(c.diasPruebaGratuita()).append(" días) si aún no la usaste. ");
            }
            if (c.puedeGestionarEnStripe()) {
                sb.append("Tienes pagos asociados: puedes gestionar la suscripción en el portal de Stripe desde Perfil. ");
            }
        }
        return sb.toString().trim();
    }

    private static String formatearFinAcceso(UsuarioResponseDTO p) {
        if (p.finSuscripcionAt() != null) {
            return FECHA_HORA.format(p.finSuscripcionAt());
        }
        if (p.finSuscripcion() != null) {
            return FECHA.format(p.finSuscripcion());
        }
        return null;
    }
}
