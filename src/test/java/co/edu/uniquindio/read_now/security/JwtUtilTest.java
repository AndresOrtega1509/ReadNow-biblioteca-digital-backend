package co.edu.uniquindio.read_now.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET = "MiClaveSecretaSuperSeguraParaJWT_ReadNow2026!@#$%^&*()_+1234567890abcdef";
    private static final long EXPIRATION = 86400000L; // 24 horas

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
    }

    @Test
    @DisplayName("10. generateToken y getClaims - genera token y extrae datos correctamente")
    void generateTokenYGetClaims_extraeDatosCorrectamente() {
        String email = "usuario@test.com";
        String rol = "LECTOR";
        Long usuarioId = 42L;

        String token = jwtUtil.generateToken(email, rol, usuarioId);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = jwtUtil.getClaims(token);
        assertEquals(email, claims.getSubject());
        assertEquals(rol, claims.get("rol", String.class));
        assertEquals(usuarioId, claims.get("usuarioId", Long.class));

        assertEquals(email, jwtUtil.getEmailFromToken(token));
        assertEquals(rol, jwtUtil.getRolFromToken(token));
        assertEquals(usuarioId, jwtUtil.getUsuarioIdFromToken(token));
        assertTrue(jwtUtil.isTokenValid(token));
    }
}
