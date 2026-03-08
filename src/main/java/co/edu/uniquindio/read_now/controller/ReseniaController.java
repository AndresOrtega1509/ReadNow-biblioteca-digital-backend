package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.ReseniaRequestDTO;
import co.edu.uniquindio.read_now.dto.response.ReseniaResponseDTO;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IReseniaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenias")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Endpoints para crear y visualizar reseñas de recursos")
public class ReseniaController {

    private final IReseniaService reseniaService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Crear reseña", description = "Permite al lector dejar una reseña sobre un recurso")
    public ResponseEntity<ReseniaResponseDTO> crearResenia(
            @Valid @RequestBody ReseniaRequestDTO request,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(reseniaService.crearResenia(request, usuarioId));
    }

    @GetMapping("/recurso/{recursoId}")
    @Operation(summary = "Reseñas del recurso", description = "Lista todas las reseñas de un recurso específico")
    public ResponseEntity<List<ReseniaResponseDTO>> obtenerResenias(@PathVariable Long recursoId) {
        return ResponseEntity.ok(reseniaService.obtenerReseniasPorRecurso(recursoId));
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUsuarioIdFromToken(token);
    }
}
