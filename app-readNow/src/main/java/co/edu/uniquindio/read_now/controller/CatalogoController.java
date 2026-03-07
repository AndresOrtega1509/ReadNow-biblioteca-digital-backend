package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.exception.SuscripcionVencidaException;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Endpoints públicos del catálogo de recursos para lectores")
public class CatalogoController {


    private final IUsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    private void verificarAccesoCatalogo(HttpServletRequest request) {
        Long usuarioId = jwtUtil.getUsuarioIdFromToken(request.getHeader("Authorization").substring(7));
        if (!usuarioService.puedeAccederAlCatalogo(usuarioId)) {
            throw new SuscripcionVencidaException();
        }
    }



}
