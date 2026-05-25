package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.LecturaAnotacionesRequestDTO;
import co.edu.uniquindio.read_now.dto.request.LecturaProgresoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.HistoriaLecturaResponseDTO;
import co.edu.uniquindio.read_now.dto.response.LecturaAnotacionesResponseDTO;
import co.edu.uniquindio.read_now.dto.response.LecturaProgresoResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.exception.SuscripcionVencidaException;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IHistoriaLecturaService;
import co.edu.uniquindio.read_now.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
@Tag(name = "Historial de Lectura", description = "Endpoints para gestionar el historial de lecturas del usuario")
public class HistoriaLecturaController {

    private final IHistoriaLecturaService historiaLecturaService;
    private final IUsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @GetMapping("/continuar")
    @Operation(summary = "Continuar leyendo", description = "Recursos con progreso reciente (máx. 12), para carrusel estilo Netflix.")
    public ResponseEntity<List<HistoriaLecturaResponseDTO>> continuarLeyendo(HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(historiaLecturaService.listarContinuarLeyendo(usuarioId));
    }

    @GetMapping("/recurso/{recursoId}/progreso")
    @Operation(summary = "Progreso en un recurso", description = "Última página y total conocido; valores null si no hay historial.")
    public ResponseEntity<LecturaProgresoResponseDTO> obtenerProgreso(
            @PathVariable Long recursoId,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(historiaLecturaService.obtenerProgreso(recursoId, usuarioId));
    }

    @PostMapping("/{recursoId}")
    @Operation(summary = "Registrar lectura", description = "Registra que el usuario leyó un recurso. Requiere suscripción activa.")
    public ResponseEntity<MensajeResponseDTO> registrarLectura(
            @PathVariable Long recursoId,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        if (!usuarioService.puedeAccederAlCatalogo(usuarioId)) {
            throw new SuscripcionVencidaException();
        }
        return ResponseEntity.ok(historiaLecturaService.registrarLectura(recursoId, usuarioId));
    }

    @GetMapping("/recurso/{recursoId}/anotaciones")
    @Operation(summary = "Anotaciones del recurso", description = "Resaltados y marcas del visor PDF guardados para el usuario.")
    public ResponseEntity<LecturaAnotacionesResponseDTO> obtenerAnotaciones(
            @PathVariable Long recursoId,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(historiaLecturaService.obtenerAnotaciones(recursoId, usuarioId));
    }

    @PutMapping("/{recursoId}/anotaciones")
    @Operation(summary = "Guardar anotaciones", description = "Persiste resaltados del visor PDF (JSON serializado sin IDs).")
    public ResponseEntity<MensajeResponseDTO> guardarAnotaciones(
            @PathVariable Long recursoId,
            @Valid @RequestBody LecturaAnotacionesRequestDTO body,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        MensajeResponseDTO res = historiaLecturaService.guardarAnotaciones(recursoId, usuarioId, body);
        return res.exitoso() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @PutMapping("/{recursoId}/progreso")
    @Operation(summary = "Guardar progreso de lectura", description = "Actualiza última página y opcionalmente el total de páginas del PDF.")
    public ResponseEntity<MensajeResponseDTO> actualizarProgreso(
            @PathVariable Long recursoId,
            @Valid @RequestBody LecturaProgresoRequestDTO body,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        // Misma regla que GET progreso/anotaciones: lectura autenticada sin bloquear por suscripción vencida en caliente
        MensajeResponseDTO res = historiaLecturaService.actualizarProgreso(recursoId, usuarioId, body);
        return res.exitoso() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @GetMapping
    @Operation(summary = "Obtener historial", description = "Lista el historial de lecturas del usuario ordenado por fecha")
    public ResponseEntity<List<HistoriaLecturaResponseDTO>> obtenerHistorial(HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(historiaLecturaService.obtenerHistorial(usuarioId));
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUsuarioIdFromToken(token);
    }
}
