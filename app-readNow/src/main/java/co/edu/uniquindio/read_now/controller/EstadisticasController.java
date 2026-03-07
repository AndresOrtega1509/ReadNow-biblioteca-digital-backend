package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.response.EstadisticasResponseDTO;
import co.edu.uniquindio.read_now.service.IEstadisticasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/estadisticas")
@RequiredArgsConstructor
@Tag(name = "Admin - Estadísticas", description = "Panel de estadísticas del administrador")
public class EstadisticasController {

    private final IEstadisticasService estadisticasService;

    @GetMapping
    @Operation(summary = "Obtener estadísticas", description = "Obtiene estadísticas generales: usuarios, recursos, lecturas, calificaciones, etc.")
    public ResponseEntity<EstadisticasResponseDTO> obtenerEstadisticas() {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticas());
    }
}
