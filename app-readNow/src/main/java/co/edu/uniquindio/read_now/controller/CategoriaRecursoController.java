package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.CategoriaRecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.CategoriaRecursoResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.service.ICategoriaRecursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categorias")
@RequiredArgsConstructor
@Tag(name = "Admin - Categorías", description = "Gestión de categorías de recursos por el administrador")
public class CategoriaRecursoController {

    private final ICategoriaRecursoService categoriaRecursoService;

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Lista todas las categorías de recursos")
    public ResponseEntity<List<CategoriaRecursoResponseDTO>> listar() {
        return ResponseEntity.ok(categoriaRecursoService.listarTodas());
    }

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría")
    public ResponseEntity<CategoriaRecursoResponseDTO> crear(@Valid @RequestBody CategoriaRecursoRequestDTO request) {
        return ResponseEntity.ok(categoriaRecursoService.crear(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza el nombre de una categoría")
    public ResponseEntity<CategoriaRecursoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRecursoRequestDTO request) {
        return ResponseEntity.ok(categoriaRecursoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría; los recursos que la usaban quedan sin categoría")
    public ResponseEntity<MensajeResponseDTO> eliminar(@PathVariable Long id) {
        categoriaRecursoService.eliminar(id);
        return ResponseEntity.ok(new MensajeResponseDTO(true, "Categoría eliminada correctamente"));
    }
}
