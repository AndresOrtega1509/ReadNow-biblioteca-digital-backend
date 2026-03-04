package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.RegistroRequestDTO;
import co.edu.uniquindio.read_now.dto.response.*;
import co.edu.uniquindio.read_now.model.Rol;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IRolRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
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
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioRepository usuarioRepository;
    private final IRolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

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


}
