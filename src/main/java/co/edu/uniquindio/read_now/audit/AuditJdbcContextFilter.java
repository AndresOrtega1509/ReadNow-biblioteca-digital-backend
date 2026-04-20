package co.edu.uniquindio.read_now.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Deja el email del usuario autenticado en {@link AuditJdbcContext} durante el request,
 * para que {@link co.edu.uniquindio.read_now.config.AuditingDataSource} establezca {@code @audit_usuario} en MySQL.
 */
@Component
public class AuditJdbcContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null
                    && auth.isAuthenticated()
                    && !(auth instanceof AnonymousAuthenticationToken)) {
                AuditJdbcContext.setUsuario(auth.getName());
            }
            filterChain.doFilter(request, response);
        } finally {
            AuditJdbcContext.clear();
        }
    }
}
