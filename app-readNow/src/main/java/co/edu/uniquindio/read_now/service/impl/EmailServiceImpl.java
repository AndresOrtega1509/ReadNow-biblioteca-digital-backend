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
    public void enviarCorreoInactividad(String email, String nombre) {

        String asunto = "ReadNow - ¡Te extrañamos!";

        String contenido = """
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2 style="color: #2c3e50;">Hola %s,</h2>
                    <p>Hemos notado que no has visitado ReadNow en los últimos días.</p>
                    <p>Tenemos nuevos recursos esperando por ti. ¡No te pierdas las últimas
                       novedades de nuestro catálogo!</p>
                    <p style="margin-top: 20px;">
                        <strong>¡Te esperamos de vuelta!</strong>
                    </p>
                    <hr>
                    <p style="color: #95a5a6; font-size: 12px;">ReadNow - Biblioteca Digital</p>
                </body>
                </html>
                """.formatted(nombre);

        enviarCorreo(email, asunto, contenido);
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