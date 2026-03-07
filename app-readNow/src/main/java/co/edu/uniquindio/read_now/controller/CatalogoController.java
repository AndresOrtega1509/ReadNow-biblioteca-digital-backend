package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.response.RecursoResponseDTO;
import co.edu.uniquindio.read_now.exception.SuscripcionVencidaException;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IRecursoService;
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

    private final IRecursoService recursoService;
    private final IUsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    private void verificarAccesoCatalogo(HttpServletRequest request) {
        Long usuarioId = jwtUtil.getUsuarioIdFromToken(request.getHeader("Authorization").substring(7));
        if (!usuarioService.puedeAccederAlCatalogo(usuarioId)) {
            throw new SuscripcionVencidaException();
        }
    }

    @GetMapping("/recursos")
    @Operation(summary = "Listar recursos", description = "Lista todos los recursos activos del catálogo")
    public ResponseEntity<List<RecursoResponseDTO>> listarRecursos(HttpServletRequest request) {
        verificarAccesoCatalogo(request);
        return ResponseEntity.ok(recursoService.listarRecursos());
    }

    @GetMapping("/recursos/{id}")
    @Operation(summary = "Obtener recurso", description = "Obtiene los detalles de un recurso específico")
    public ResponseEntity<RecursoResponseDTO> obtenerRecurso(@PathVariable Long id, HttpServletRequest request) {
        verificarAccesoCatalogo(request);
        return ResponseEntity.ok(recursoService.obtenerRecurso(id));
    }

    @GetMapping("/recursos/buscar")
    @Operation(summary = "Buscar recursos", description = "Busca recursos por nombre")
    public ResponseEntity<List<RecursoResponseDTO>> buscarRecursos(@RequestParam String q, HttpServletRequest request) {
        verificarAccesoCatalogo(request);
        return ResponseEntity.ok(recursoService.buscarRecursos(q));
    }

    @GetMapping("/recursos/tipo/{tipoRecursoId}")
    @Operation(summary = "Por tipo de recurso", description = "Lista recursos filtrados por tipo (libro, tesis, revista, etc.)")
    public ResponseEntity<List<RecursoResponseDTO>> listarPorTipo(@PathVariable Long tipoRecursoId, HttpServletRequest request) {
        verificarAccesoCatalogo(request);
        return ResponseEntity.ok(recursoService.listarPorTipo(tipoRecursoId));
    }

    @GetMapping("/recursos/categoria/{categoriaId}")
    @Operation(summary = "Por categoría", description = "Lista recursos filtrados por categoría")
    public ResponseEntity<List<RecursoResponseDTO>> listarPorCategoria(@PathVariable Long categoriaId, HttpServletRequest request) {
        verificarAccesoCatalogo(request);
        return ResponseEntity.ok(recursoService.listarPorCategoria(categoriaId));
    }

}
