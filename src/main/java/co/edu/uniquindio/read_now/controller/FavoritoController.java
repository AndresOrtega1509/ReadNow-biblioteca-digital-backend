package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.response.FavoritoResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IFavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
@Tag(name = "Favoritos", description = "Endpoints para gestionar la lista de recursos favoritos del usuario")
public class FavoritoController {

    private final IFavoritoService favoritoService;
    private final JwtUtil jwtUtil;

    @PostMapping("/{recursoId}")
    @Operation(summary = "Agregar a favoritos", description = "Agrega un recurso a la lista de favoritos del usuario")
    public ResponseEntity<MensajeResponseDTO> agregarFavorito(
            @PathVariable Long recursoId,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        MensajeResponseDTO response = favoritoService.agregarFavorito(recursoId, usuarioId);
        return response.exitoso() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{favoritoId}")
    @Operation(summary = "Eliminar de favoritos", description = "Elimina un recurso de la lista de favoritos del usuario")
    public ResponseEntity<MensajeResponseDTO> eliminarFavorito(
            @PathVariable Long favoritoId,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        MensajeResponseDTO response = favoritoService.eliminarFavorito(favoritoId, usuarioId);
        return response.exitoso() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping
    @Operation(summary = "Listar favoritos", description = "Lista todos los recursos favoritos del usuario")
    public ResponseEntity<List<FavoritoResponseDTO>> listarFavoritos(HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(favoritoService.listarFavoritos(usuarioId));
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUsuarioIdFromToken(token);
    }
}
