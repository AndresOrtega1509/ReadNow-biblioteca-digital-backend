package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.*;
import co.edu.uniquindio.read_now.dto.response.*;
import co.edu.uniquindio.read_now.model.Rol;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IRolRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioRepository usuarioRepository;
    private final IRolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final IEmailService emailService;

    @Value("${app.trial.days}")
    private int trialDays;

    @Value("${app.session.inactivity-reading-ms}")
    private long inactividadLecturaMs;

    @Value("${app.session.inactivity-catalog-ms}")
    private long inactividadCatalogoMs;

    @Value("${app.session.countdown-ms}")
    private long countdownMs;

    @Value("${app.auth.two-factor-enabled:true}")
    private boolean twoFactorEnabled;

    @Value("${app.recuperacion.minutos:15}")
    private int recuperacionMinutos;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    private final Map<String, CodigoVerificacionEntry> codigosVerificacion = new ConcurrentHashMap<>();

    private record CodigoVerificacionEntry(String codigo, LocalDateTime expiracion) {}

    @Override
    @Transactional
    public MensajeResponseDTO registrar(RegistroRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            return new MensajeResponseDTO(false, "Ya existe un usuario registrado con ese email");
        }
        if (usuarioRepository.existsByUsername(request.username())) {
            return new MensajeResponseDTO(false, "Ya existe un usuario con ese nombre de usuario");
        }

        Rol rolLector = rolRepository.findByNombre("LECTOR")
                .orElseThrow(() -> new RuntimeException("Rol LECTOR no encontrado en el sistema"));

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .email(request.email())
                .telefono(request.telefono())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .fechaRegistro(LocalDate.now())
                .activo("S")
                .inicioSuscripcion(LocalDate.now())
                .finSuscripcion(LocalDate.now().plusDays(trialDays))
                .ultimoAcceso(LocalDateTime.now())
                .rol(rolLector)
                .suscripcionVencidaNotificada(false)
                .twoFactorActivo(true)
                .build();

        usuarioRepository.save(usuario);
        log.info("Usuario registrado: {}", request.email());

        return new MensajeResponseDTO(true,
                "Registro exitoso. Tienes una prueba gratuita de " + trialDays + " días.");
    }

    @Override
    @Transactional
    public LoginResultDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElse(null);

        if (usuario == null || !passwordEncoder.matches(request.password(), usuario.getPassword())) {
            return LoginResultDTO.conMensaje(false, "Credenciales inválidas");
        }

        if (!"S".equals(usuario.getActivo())) {
            return LoginResultDTO.conMensaje(false, "La cuenta se encuentra desactivada");
        }

        // Si 2FA está desactivado (desarrollo), devolver token directo sin pedir código
        if (!twoFactorEnabled) {
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);

            String token = jwtUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getRol().getNombre(),
                    usuario.getUsuarioId()
            );
            SesionConfigResponseDTO sesionConfig = new SesionConfigResponseDTO(
                    inactividadLecturaMs,
                    inactividadCatalogoMs,
                    countdownMs
            );
            LoginResponseDTO loginResponse = new LoginResponseDTO(
                    token,
                    usuario.getEmail(),
                    usuario.getRol().getNombre(),
                    usuario.getNombre() + " " + usuario.getApellido(),
                    usuario.getUsuarioId(),
                    sesionConfig
            );
            log.info("Login sin 2FA (desarrollo): {}", usuario.getEmail());
            return LoginResultDTO.conToken(loginResponse);
        }

        // 2FA activado: enviar código por correo
        String codigo = generarCodigoVerificacion();
        codigosVerificacion.put(request.email(),
                new CodigoVerificacionEntry(codigo, LocalDateTime.now().plusMinutes(5)));

        emailService.enviarCodigoVerificacion(request.email(), usuario.getNombre(), codigo);
        log.info("Código de verificación enviado a: {}", request.email());

        return LoginResultDTO.conMensaje(true,
                "Se ha enviado un código de verificación a tu correo electrónico");
    }

    @Override
    @Transactional
    public LoginResponseDTO verificarCodigo(VerificacionCodigoRequestDTO request) {
        CodigoVerificacionEntry entry = codigosVerificacion.get(request.email());

        if (entry == null) {
            throw new RuntimeException("No hay un código de verificación pendiente para este email");
        }

        if (LocalDateTime.now().isAfter(entry.expiracion())) {
            codigosVerificacion.remove(request.email());
            throw new RuntimeException("El código de verificación ha expirado. Inicia sesión nuevamente");
        }

        if (!entry.codigo().equals(request.codigo())) {
            throw new RuntimeException("Código de verificación incorrecto");
        }

        codigosVerificacion.remove(request.email());

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                usuario.getUsuarioId()
        );

        SesionConfigResponseDTO sesionConfig = new SesionConfigResponseDTO(
                inactividadLecturaMs,
                inactividadCatalogoMs,
                countdownMs
        );

        return new LoginResponseDTO(
                token,
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getUsuarioId(),
                sesionConfig
        );
    }

    @Override
    public MensajeResponseDTO recuperarPassword(RecuperarPasswordRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElse(null);

        if (usuario == null) {
            return new MensajeResponseDTO(true,
                    "Si el correo está registrado, recibirás un enlace de recuperación");
        }

        String token = jwtUtil.generateRecoveryToken(request.email());
        emailService.enviarTokenRecuperacion(request.email(), usuario.getNombre(), token);
        log.info("Token de recuperación enviado a: {}", request.email());

        return new MensajeResponseDTO(true,
                "Si el correo está registrado, recibirás un enlace de recuperación");
    }

    @Override
    @Transactional
    public MensajeResponseDTO restablecerPassword(RestablecerPasswordRequestDTO request) {
        if (!jwtUtil.isTokenValid(request.token())) {
            return new MensajeResponseDTO(false, "El token es inválido o ha expirado");
        }

        if (!jwtUtil.isRecoveryToken(request.token())) {
            return new MensajeResponseDTO(false, "El token no es un token de recuperación válido");
        }

        String email = jwtUtil.getEmailFromToken(request.token());
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPassword(passwordEncoder.encode(request.nuevaPassword()));
        usuarioRepository.save(usuario);
        log.info("Contraseña restablecida para: {}", email);

        return new MensajeResponseDTO(true, "Contraseña actualizada exitosamente");
    }

    private String generarCodigoVerificacion() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }


}
