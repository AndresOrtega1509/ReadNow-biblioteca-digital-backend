package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.CambiarPasswordRequestDTO;
import co.edu.uniquindio.read_now.model.Rol;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private INotificadorSuscripcionVencidaService notificadorSuscripcionVencida;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioAdmin;
    private Usuario usuarioLectorConSuscripcion;
    private Usuario usuarioLectorSinSuscripcion;
    private Rol rolAdmin;
    private Rol rolLector;

    @BeforeEach
    void setUp() {
        rolAdmin = Rol.builder().rolId(1L).nombre("ADMIN").build();
        rolLector = Rol.builder().rolId(2L).nombre("LECTOR").build();

        usuarioAdmin = Usuario.builder()
                .usuarioId(1L)
                .email("admin@test.com")
                .rol(rolAdmin)
                .finSuscripcion(LocalDate.now().minusDays(1))
                .finSuscripcionAt(null)
                .suscripcionVencidaNotificada(true)
                .build();

        usuarioLectorConSuscripcion = Usuario.builder()
                .usuarioId(2L)
                .email("lector@test.com")
                .username("lector_test")
                .telefono("3001234567")
                .nombre("Juan")
                .apellido("Lector")
                .fechaRegistro(LocalDate.now())
                .inicioSuscripcion(LocalDate.now())
                .rol(rolLector)
                .finSuscripcion(LocalDate.now().plusDays(7))
                .finSuscripcionAt(null)
                .suscripcionVencidaNotificada(false)
                .password("$2a$10$hashedPasswordFromDb")
                .twoFactorActivo(false)
                .build();

        usuarioLectorSinSuscripcion = Usuario.builder()
                .usuarioId(3L)
                .email("vencido@test.com")
                .rol(rolLector)
                .finSuscripcion(LocalDate.now().minusDays(1))
                .finSuscripcionAt(null)
                .suscripcionVencidaNotificada(true)
                .build();

    }

    @Test
    @DisplayName("1. puedeAccederAlCatalogo - ADMIN siempre tiene acceso")
    void puedeAccederAlCatalogo_adminSiempreTieneAcceso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioAdmin));

        boolean resultado = usuarioService.puedeAccederAlCatalogo(1L);

        assertTrue(resultado);
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("2. puedeAccederAlCatalogo - LECTOR con suscripción activa tiene acceso")
    void puedeAccederAlCatalogo_lectorConSuscripcionActivaTieneAcceso() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioLectorConSuscripcion));

        boolean resultado = usuarioService.puedeAccederAlCatalogo(2L);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("3. puedeAccederAlCatalogo - LECTOR con suscripción vencida no tiene acceso")
    void puedeAccederAlCatalogo_lectorConSuscripcionVencidaNoTieneAcceso() {
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuarioLectorSinSuscripcion));

        boolean resultado = usuarioService.puedeAccederAlCatalogo(3L);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("4. actualizarVerificacionDosPasos - activa 2FA y devuelve perfil actualizado")
    void actualizarVerificacionDosPasos_activa2FAYDevuelvePerfil() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioLectorConSuscripcion));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        var resultado = usuarioService.actualizarVerificacionDosPasos(2L, true);

        assertNotNull(resultado);
        assertTrue(resultado.twoFactorActivo());
        verify(usuarioRepository).save(argThat(u -> Boolean.TRUE.equals(u.getTwoFactorActivo())));
    }

    @Test
    @DisplayName("5. cambiarPassword - lanza excepción cuando contraseña actual es incorrecta")
    void cambiarPassword_contraseñaActualIncorrectaLanzaExcepcion() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioLectorConSuscripcion));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        CambiarPasswordRequestDTO request = new CambiarPasswordRequestDTO(
                "contraseñaIncorrecta",
                "nuevaPassword123",
                "nuevaPassword123"
        );

        assertThrows(IllegalArgumentException.class, () ->
                usuarioService.cambiarPassword(2L, request)
        );
        verify(usuarioRepository).findById(2L);
        verify(usuarioRepository, never()).save(any());
    }
}
