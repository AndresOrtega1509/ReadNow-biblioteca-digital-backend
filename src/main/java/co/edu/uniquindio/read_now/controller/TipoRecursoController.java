package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.TipoRecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.dto.response.TipoRecursoResponseDTO;
import co.edu.uniquindio.read_now.service.ITipoRecursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tipos-recurso")
@RequiredArgsConstructor
@Tag(name = "Admin - Tipos de Recurso", description = "Gestión de tipos de recursos por el administrador")
public class TipoRecursoController {

    private final ITipoRecursoService tipoRecursoService;

    @GetMapping
    @Operation(summary = "Listar tipos", description = "Lista todos los tipos de recursos")
    public ResponseEntity<List<TipoRecursoResponseDTO>> listar() {
        return ResponseEntity.ok(tipoRecursoService.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Crear tipo", description = "Crea un nuevo tipo de recurso")
    public ResponseEntity<TipoRecursoResponseDTO> crear(@Valid @RequestBody TipoRecursoRequestDTO request) {
        return ResponseEntity.ok(tipoRecursoService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo", description = "Actualiza el nombre de un tipo de recurso")
    public ResponseEntity<TipoRecursoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody TipoRecursoRequestDTO request) {
        return ResponseEntity.ok(tipoRecursoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo", description = "Elimina un tipo de recurso; los recursos que lo usaban quedan sin tipo")
    public ResponseEntity<MensajeResponseDTO> eliminar(@PathVariable Long id) {
        tipoRecursoService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponseDTO(true, "Tipo de recurso eliminado correctamente"));
    }
}
