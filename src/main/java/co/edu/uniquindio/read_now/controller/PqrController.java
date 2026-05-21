package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.PqrCrearRequestDTO;
import co.edu.uniquindio.read_now.dto.request.PqrMensajeRequestDTO;
import co.edu.uniquindio.read_now.dto.response.PqrDetalleResponseDTO;
import co.edu.uniquindio.read_now.dto.response.PqrResumenResponseDTO;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IPqrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pqr")
@RequiredArgsConstructor
@Tag(name = "PQR", description = "Peticiones, quejas y reclamos del lector")
public class PqrController {

    private final IPqrService pqrService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Crear PQR", description = "Registra una nueva petición, queja, reclamo o sugerencia")
    public ResponseEntity<PqrDetalleResponseDTO> crear(
            @Valid @RequestBody PqrCrearRequestDTO request,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(pqrService.crearPqr(usuarioId, request));
    }

    @GetMapping
    @Operation(summary = "Mis PQRs", description = "Lista las PQRs del lector autenticado")
    public ResponseEntity<List<PqrResumenResponseDTO>> listarMisPqrs(HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(pqrService.listarMisPqrs(usuarioId));
    }

    @GetMapping("/{pqrId}")
    @Operation(summary = "Detalle PQR", description = "Obtiene el detalle y mensajes de una PQR propia")
    public ResponseEntity<PqrDetalleResponseDTO> detalle(
            @PathVariable Long pqrId,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(pqrService.obtenerDetalleLector(usuarioId, pqrId));
    }

    @PostMapping("/{pqrId}/mensajes")
    @Operation(summary = "Agregar mensaje", description = "El lector agrega un mensaje a su PQR")
    public ResponseEntity<PqrDetalleResponseDTO> agregarMensaje(
            @PathVariable Long pqrId,
            @Valid @RequestBody PqrMensajeRequestDTO request,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(pqrService.agregarMensajeLector(usuarioId, pqrId, request));
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUsuarioIdFromToken(token);
    }
}
