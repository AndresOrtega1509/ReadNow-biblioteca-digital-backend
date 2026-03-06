package co.edu.uniquindio.read_now.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String rol, Long usuarioId) {
        return Jwts.builder()
                .subject(email)
                .claims(Map.of("rol", rol, "usuarioId", usuarioId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRecoveryToken(String email) {
        long fifteenMinutes = 15 * 60 * 1000L;
        return Jwts.builder()
                .subject(email)
                .claims(Map.of("type", "recovery"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + fifteenMinutes))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRolFromToken(String token) {
        return getClaims(token).get("rol", String.class);
    }

    public Long getUsuarioIdFromToken(String token) {
        return getClaims(token).get("usuarioId", Long.class);
    }

    public boolean isRecoveryToken(String token) {
        return "recovery".equals(getClaims(token).get("type", String.class));
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
