package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.RecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.request.TipoRecursoRequestDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.dto.response.RecursoResponseDTO;
import co.edu.uniquindio.read_now.dto.response.TipoRecursoResponseDTO;
import co.edu.uniquindio.read_now.service.IRecursoService;
import co.edu.uniquindio.read_now.service.ITipoRecursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/recursos")
@RequiredArgsConstructor
@Tag(name = "Admin - Recursos", description = "Panel de administrador para gestión de recursos del catálogo")
public class RecursoController {

    private final IRecursoService recursoService;
    private final ITipoRecursoService tipoRecursoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear recurso", description = "Sube PDF y/o portada a Firebase Storage y crea el recurso")
    public ResponseEntity<RecursoResponseDTO> crearRecurso(
            @Valid @RequestPart("recurso") RecursoRequestDTO request,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo,
            @RequestPart(value = "portada", required = false) MultipartFile portada) {
        return ResponseEntity.ok(recursoService.crearRecurso(request, archivo, portada));
    }


}
