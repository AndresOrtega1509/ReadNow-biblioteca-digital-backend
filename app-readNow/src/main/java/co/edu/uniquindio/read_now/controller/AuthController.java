package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.RegistroRequestDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de registro, login y recuperación de contraseña")
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario", description = "Registra un usuario con rol LECTOR y prueba gratuita de 15 días")
    public ResponseEntity<MensajeResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        MensajeResponseDTO response = authService.registrar(request);
        return response.exitoso() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

}
