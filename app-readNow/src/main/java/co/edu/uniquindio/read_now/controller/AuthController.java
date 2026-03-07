package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.*;
import co.edu.uniquindio.read_now.dto.response.LoginResponseDTO;
import co.edu.uniquindio.read_now.dto.response.LoginResultDTO;
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


    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Con 2FA: envía código por correo. Sin 2FA (desarrollo): devuelve token directo.")
    public ResponseEntity<LoginResultDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResultDTO response = authService.login(request);
        return response.exitoso() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/verificar")
    @Operation(summary = "Verificar código 2FA", description = "Verifica el código enviado por correo y genera token JWT (24h expiración)")
    public ResponseEntity<LoginResponseDTO> verificarCodigo(@Valid @RequestBody VerificacionCodigoRequestDTO request) {
        LoginResponseDTO response = authService.verificarCodigo(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recuperar")
    @Operation(summary = "Solicitar recuperación de contraseña", description = "Envía un token de recuperación al correo (válido por 15 minutos)")
    public ResponseEntity<MensajeResponseDTO> recuperarPassword(@Valid @RequestBody RecuperarPasswordRequestDTO request) {
        MensajeResponseDTO response = authService.recuperarPassword(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/restablecer")
    @Operation(summary = "Restablecer contraseña", description = "Actualiza la contraseña usando el token de recuperación")
    public ResponseEntity<MensajeResponseDTO> restablecerPassword(@Valid @RequestBody RestablecerPasswordRequestDTO request) {
        MensajeResponseDTO response = authService.restablecerPassword(request);
        return response.exitoso() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
    @PostMapping("/recuperar/verificar-telefono")
    @Operation(summary = "Paso 2: Verificar últimos 4 dígitos", description = "Si coinciden, se envía por correo un enlace para restablecer la contraseña (válido 15 min).")
    public ResponseEntity<MensajeResponseDTO> verificarTelefonoRecuperacion(@Valid @RequestBody VerificarTelefonoRecuperacionRequestDTO request) {
        MensajeResponseDTO response = authService.verificarTelefonoRecuperacion(request);
        return response.exitoso() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
}
