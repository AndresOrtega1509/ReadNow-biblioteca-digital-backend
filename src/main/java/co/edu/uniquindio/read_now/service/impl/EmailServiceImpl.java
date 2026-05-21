package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.service.IEmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    // Ahora usamos directamente el username como remitente
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Async // Opcional pero recomendado
    @Override
    public void enviarCodigoVerificacion(String email, String nombre, String codigo) {

        String asunto = "ReadNow - Código de verificación";

        String contenido = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2 style="color: #2c3e50;">Hola %s,</h2>
                    <p>Tu código de verificación para iniciar sesión es:</p>
                    <div style="background-color: #3498db; color: white; padding: 15px 30px;
                                font-size: 24px; font-weight: bold; text-align: center;
                                border-radius: 8px; display: inline-block; letter-spacing: 5px;">
                        %s
                    </div>
                    <p style="color: #e74c3c; margin-top: 15px;">
                        <strong>Este código es válido por 5 minutos.</strong>
                    </p>
                    <p>Si no solicitaste este código, ignora este mensaje.</p>
                    <hr>
                    <p style="color: #95a5a6; font-size: 12px;">ReadNow - Biblioteca Digital</p>
                </body>
                </html>
                """.formatted(nombre, codigo);

        enviarCorreo(email, asunto, contenido);
    }

    @Async
    @Override
    public void enviarEnlaceRecuperacion(String email, String nombre, String urlEnlace) {
        String asunto = "ReadNow - Restablecer contraseña";

        String contenido = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2 style="color: #2c3e50;">Hola %s,</h2>
                    <p>Has solicitado recuperar tu contraseña y verificaste los últimos 4 dígitos de tu teléfono.</p>
                    <p>Haz clic en el siguiente enlace para elegir una nueva contraseña (válido por 15 minutos):</p>
                    <p style="margin: 24px 0;">
                        <a href="%s" style="background-color: #3498db; color: white; padding: 12px 24px; text-decoration: none;
                           border-radius: 8px; font-weight: bold; display: inline-block;">Restablecer contraseña</a>
                    </p>
                    <p style="color: #e74c3c; margin-top: 15px;"><strong>Si no solicitaste este cambio, ignora este mensaje.</strong></p>
                    <hr>
                    <p style="color: #95a5a6; font-size: 12px;">ReadNow - Biblioteca Digital</p>
                </body>
                </html>
                """.formatted(nombre, urlEnlace);

        enviarCorreo(email, asunto, contenido);
    }

    @Async
    @Override
    public void enviarTokenRecuperacion(String email, String nombre, String token) {

        String asunto = "ReadNow - Recuperación de contraseña";

        String contenido = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2 style="color: #2c3e50;">Hola %s,</h2>
                    <p>Has solicitado recuperar tu contraseña. Usa el siguiente token:</p>
                    <div style="background-color: #f39c12; color: white; padding: 10px 20px;
                                font-size: 14px; text-align: center; border-radius: 8px;
                                word-break: break-all;">
                        %s
                    </div>
                    <p style="color: #e74c3c; margin-top: 15px;">
                        <strong>Este token es válido por 15 minutos.</strong>
                    </p>
                    <p>Si no solicitaste esta recuperación, ignora este mensaje.</p>
                    <hr>
                    <p style="color: #95a5a6; font-size: 12px;">ReadNow - Biblioteca Digital</p>
                </body>
                </html>
                """.formatted(nombre, token);

        enviarCorreo(email, asunto, contenido);
    }

    @Async
    @Override
    public void enviarCorreoInactividad(String email, String nombre, int diasInactividad) {

        String asunto = "ReadNow - ¡Te extrañamos!";

        String contenido = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2 style="color: #2c3e50;">Hola %s,</h2>
                    <p>Hemos notado que no has visitado ReadNow en más de %d días.</p>
                    <p>Tenemos nuevos recursos esperando por ti. ¡No te pierdas las últimas
                       novedades de nuestro catálogo!</p>
                    <p style="margin-top: 20px;">
                        <strong>¡Te esperamos de vuelta!</strong>
                    </p>
                    <hr>
                    <p style="color: #95a5a6; font-size: 12px;">ReadNow - Biblioteca Digital</p>
                </body>
                </html>
                """.formatted(nombre, diasInactividad);

        enviarCorreo(email, asunto, contenido);
    }

    @Async
    @Override
    public void enviarCorreoSuscripcionVencida(String email, String nombre) {
        String asunto = "ReadNow - Tu suscripción ha vencido";

        String contenido = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2 style="color: #2c3e50;">Hola %s,</h2>
                    <p>Te informamos que tu suscripción o prueba gratuita en ReadNow ha vencido.</p>
                    <p>Para seguir disfrutando de nuestro catálogo de recursos y poder leer libros, revistas y más,
                       necesitas renovar tu suscripción.</p>
                    <p style="margin-top: 20px;">
                        <strong>Inicia sesión en tu perfil para ver las opciones de renovación.</strong>
                    </p>
                    <p>¡Te esperamos de vuelta en ReadNow!</p>
                    <hr>
                    <p style="color: #95a5a6; font-size: 12px;">ReadNow - Biblioteca Digital</p>
                </body>
                </html>
                """.formatted(nombre);

        enviarCorreo(email, asunto, contenido);
    }

    @Async
    @Override
    public void enviarRecordatorioSuscripcionPorVencer(String email, String nombre, int diasRestantes) {
        String diasTexto = diasRestantes == 1 ? "1 día" : diasRestantes + " días";
        String asunto = "ReadNow - Tu suscripción vence en " + diasTexto;

        String contenido = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2 style="color: #2c3e50;">Hola %s,</h2>
                    <p>Te recordamos que tu suscripción o prueba gratuita en ReadNow vence en <strong>%s</strong>.</p>
                    <p>Para no perder el acceso al catálogo de recursos, libros, revistas y más, te recomendamos renovar tu suscripción antes de que expire.</p>
                    <p style="margin-top: 20px;">
                        <strong>Inicia sesión en tu perfil para renovar y seguir disfrutando de ReadNow.</strong>
                    </p>
                    <hr>
                    <p style="color: #95a5a6; font-size: 12px;">ReadNow - Biblioteca Digital</p>
                </body>
                </html>
                """.formatted(nombre, diasTexto);

        enviarCorreo(email, asunto, contenido);
    }

    @Async
    @Override
    public void enviarPqrRecibidaLector(String email, String nombre, Long pqrId, String asunto) {
        String link = frontendUrl + "/pqr";
        String asuntoCorreo = "ReadNow - Tu PQR #" + pqrId + " fue registrada";
        String contenido = plantillaPqr("""
                <h2 style="color: #2c3e50;">Hola %s,</h2>
                <p>Hemos recibido tu solicitud <strong>#%d</strong> con asunto <em>%s</em>.</p>
                <p>Estado actual: <strong>Abierta</strong>. Te notificaremos por correo cuando haya novedades.</p>
                <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #6c5ce7; color: white; padding: 12px 24px; text-decoration: none;
                       border-radius: 8px; font-weight: bold; display: inline-block;">Ver mis PQRs</a>
                </p>
                """.formatted(nombre, pqrId, escaparHtml(asunto), link));
        enviarCorreo(email, asuntoCorreo, contenido);
    }

    @Async
    @Override
    public void enviarPqrNuevaAdmin(String email, String nombreAdmin, Long pqrId, String asunto, String tipo,
                                    String lectorNombre, String lectorEmail, String descripcion) {
        String link = frontendUrl + "/admin/pqr";
        String asuntoCorreo = "ReadNow - Nueva PQR #" + pqrId + " (" + tipo + ")";
        String descCorta = descripcion.length() > 400 ? descripcion.substring(0, 400) + "…" : descripcion;
        String contenido = plantillaPqr("""
                <h2 style="color: #2c3e50;">Hola %s,</h2>
                <p>Se registró una nueva PQR que requiere gestión:</p>
                <ul style="line-height: 1.6;">
                    <li><strong>ID:</strong> #%d</li>
                    <li><strong>Tipo:</strong> %s</li>
                    <li><strong>Asunto:</strong> %s</li>
                    <li><strong>Lector:</strong> %s (%s)</li>
                </ul>
                <p style="background: #f4f4f5; padding: 12px; border-radius: 8px;">%s</p>
                <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #6c5ce7; color: white; padding: 12px 24px; text-decoration: none;
                       border-radius: 8px; font-weight: bold; display: inline-block;">Gestionar PQRs</a>
                </p>
                """.formatted(
                nombreAdmin, pqrId, tipo, escaparHtml(asunto), escaparHtml(lectorNombre),
                escaparHtml(lectorEmail), escaparHtml(descCorta), link));
        enviarCorreo(email, asuntoCorreo, contenido);
    }

    @Async
    @Override
    public void enviarPqrCambioEstadoLector(String email, String nombre, Long pqrId, String asunto,
                                            String estadoAnterior, String estadoNuevo, String mensajeAdmin) {
        String link = frontendUrl + "/pqr";
        String asuntoCorreo = "ReadNow - Actualización PQR #" + pqrId;
        String bloqueMensaje = (mensajeAdmin != null && !mensajeAdmin.isBlank())
                ? "<p style=\"background: #f4f4f5; padding: 12px; border-radius: 8px;\"><strong>Mensaje del equipo:</strong><br/>"
                + escaparHtml(mensajeAdmin) + "</p>"
                : "";
        String contenido = plantillaPqr("""
                <h2 style="color: #2c3e50;">Hola %s,</h2>
                <p>Tu PQR <strong>#%d</strong> (<em>%s</em>) cambió de estado:</p>
                <p><strong>%s</strong> → <strong>%s</strong></p>
                %s
                <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #6c5ce7; color: white; padding: 12px 24px; text-decoration: none;
                       border-radius: 8px; font-weight: bold; display: inline-block;">Ver detalle</a>
                </p>
                """.formatted(
                nombre, pqrId, escaparHtml(asunto), estadoAnterior, estadoNuevo, bloqueMensaje, link));
        enviarCorreo(email, asuntoCorreo, contenido);
    }

    @Async
    @Override
    public void enviarPqrRespuestaAdminLector(String email, String nombre, Long pqrId, String asunto, String mensaje) {
        String link = frontendUrl + "/pqr";
        String asuntoCorreo = "ReadNow - Respuesta a tu PQR #" + pqrId;
        String contenido = plantillaPqr("""
                <h2 style="color: #2c3e50;">Hola %s,</h2>
                <p>El equipo de ReadNow respondió tu PQR <strong>#%d</strong> (<em>%s</em>):</p>
                <p style="background: #f4f4f5; padding: 12px; border-radius: 8px;">%s</p>
                <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #6c5ce7; color: white; padding: 12px 24px; text-decoration: none;
                       border-radius: 8px; font-weight: bold; display: inline-block;">Ver conversación</a>
                </p>
                """.formatted(nombre, pqrId, escaparHtml(asunto), escaparHtml(mensaje), link));
        enviarCorreo(email, asuntoCorreo, contenido);
    }

    @Async
    @Override
    public void enviarPqrMensajeLectorAdmin(String email, String nombreAdmin, Long pqrId, String asunto,
                                            String lectorNombre, String mensaje) {
        String link = frontendUrl + "/admin/pqr";
        String asuntoCorreo = "ReadNow - Nuevo mensaje en PQR #" + pqrId;
        String contenido = plantillaPqr("""
                <h2 style="color: #2c3e50;">Hola %s,</h2>
                <p><strong>%s</strong> escribió en la PQR <strong>#%d</strong> (<em>%s</em>):</p>
                <p style="background: #f4f4f5; padding: 12px; border-radius: 8px;">%s</p>
                <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #6c5ce7; color: white; padding: 12px 24px; text-decoration: none;
                       border-radius: 8px; font-weight: bold; display: inline-block;">Responder en el panel</a>
                </p>
                """.formatted(
                nombreAdmin, escaparHtml(lectorNombre), pqrId, escaparHtml(asunto), escaparHtml(mensaje), link));
        enviarCorreo(email, asuntoCorreo, contenido);
    }

    private static String plantillaPqr(String cuerpo) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                %s
                <hr>
                <p style="color: #95a5a6; font-size: 12px;">ReadNow - Biblioteca Digital</p>
                </body>
                </html>
                """.formatted(cuerpo);
    }

    private static String escaparHtml(String texto) {
        if (texto == null) {
            return "";
        }
        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void enviarCorreo(String to, String subject, String htmlContent) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Correo enviado exitosamente a: {}", to);

        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error al enviar el correo electrónico", e);
        }
    }

}