package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.PqrActualizarEstadoRequestDTO;
import co.edu.uniquindio.read_now.dto.request.PqrMensajeRequestDTO;
import co.edu.uniquindio.read_now.dto.response.PqrAdminResumenResponseDTO;
import co.edu.uniquindio.read_now.dto.response.PqrDetalleResponseDTO;
import co.edu.uniquindio.read_now.model.enums.EstadoPqr;
import co.edu.uniquindio.read_now.model.enums.TipoPqr;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IPqrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pqr")
@RequiredArgsConstructor
@Tag(name = "Admin - PQR", description = "Gestión de peticiones, quejas y reclamos")
public class AdminPqrController {

    private final IPqrService pqrService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "Listar PQRs", description = "Lista todas las PQRs con filtros opcionales")
    public ResponseEntity<List<PqrAdminResumenResponseDTO>> listar(
            @RequestParam(required = false) EstadoPqr estado,
            @RequestParam(required = false) TipoPqr tipo) {
        return ResponseEntity.ok(pqrService.listarPqrsAdmin(estado, tipo));
    }

    @GetMapping("/{pqrId}")
    @Operation(summary = "Detalle PQR", description = "Detalle completo de una PQR")
    public ResponseEntity<PqrDetalleResponseDTO> detalle(@PathVariable Long pqrId) {
        return ResponseEntity.ok(pqrService.obtenerDetalleAdmin(pqrId));
    }

    @PutMapping("/{pqrId}/estado")
    @Operation(summary = "Actualizar estado", description = "Cambia el estado y opcionalmente agrega un mensaje")
    public ResponseEntity<PqrDetalleResponseDTO> actualizarEstado(
            @PathVariable Long pqrId,
            @Valid @RequestBody PqrActualizarEstadoRequestDTO request,
            HttpServletRequest httpRequest) {
        Long adminId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(pqrService.actualizarEstadoAdmin(adminId, pqrId, request));
    }

    @PostMapping("/{pqrId}/mensajes")
    @Operation(summary = "Responder PQR", description = "El administrador responde al lector")
    public ResponseEntity<PqrDetalleResponseDTO> responder(
            @PathVariable Long pqrId,
            @Valid @RequestBody PqrMensajeRequestDTO request,
            HttpServletRequest httpRequest) {
        Long adminId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(pqrService.agregarMensajeAdmin(adminId, pqrId, request));
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUsuarioIdFromToken(token);
    }
}
