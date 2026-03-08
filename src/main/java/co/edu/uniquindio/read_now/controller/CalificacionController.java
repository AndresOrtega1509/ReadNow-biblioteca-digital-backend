package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.CalificacionRequestDTO;
import co.edu.uniquindio.read_now.dto.response.CalificacionResponseDTO;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.ICalificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
@Tag(name = "Calificaciones", description = "Endpoints para calificar recursos (1 a 5 estrellas)")
public class CalificacionController {

    private final ICalificacionService calificacionService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Calificar recurso", description = "Permite al lector calificar un recurso de 1 a 5")
    public ResponseEntity<CalificacionResponseDTO> calificar(
            @Valid @RequestBody CalificacionRequestDTO request,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(calificacionService.calificar(request, usuarioId));
    }

    @GetMapping("/recurso/{recursoId}/promedio")
    @Operation(summary = "Promedio de calificación", description = "Obtiene la calificación promedio de un recurso")
    public ResponseEntity<Double> obtenerPromedio(@PathVariable Long recursoId) {
        return ResponseEntity.ok(calificacionService.obtenerPromedioCalificacion(recursoId));
    }

    @GetMapping("/recurso/{recursoId}/mi-calificacion")
    @Operation(summary = "Mi calificación", description = "Obtiene la calificación del usuario autenticado para el recurso (0 si no ha calificado)")
    public ResponseEntity<Integer> obtenerMiCalificacion(
            @PathVariable Long recursoId,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(calificacionService.obtenerMiCalificacion(recursoId, usuarioId));
    }

    @GetMapping("/recurso/{recursoId}")
    @Operation(summary = "Calificaciones del recurso", description = "Lista todas las calificaciones de un recurso")
    public ResponseEntity<List<CalificacionResponseDTO>> obtenerCalificaciones(@PathVariable Long recursoId) {
        return ResponseEntity.ok(calificacionService.obtenerCalificacionesPorRecurso(recursoId));
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUsuarioIdFromToken(token);
    }
}
