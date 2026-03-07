package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.ActualizarPerfilRequestDTO;
import co.edu.uniquindio.read_now.dto.request.CambiarPasswordRequestDTO;
import co.edu.uniquindio.read_now.dto.request.VerificacionDosPasosRequestDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.dto.response.UsuarioResponseDTO;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints de gestión del perfil y sesión del usuario")
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @GetMapping("/perfil")
    @Operation(summary = "Obtener perfil", description = "Obtiene el perfil del usuario autenticado")
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil(HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(usuarioService.obtenerPerfil(usuarioId));
    }

    @PutMapping("/perfil")
    @Operation(summary = "Actualizar perfil", description = "Actualiza nombre, apellido, teléfono y username del usuario")
    public ResponseEntity<UsuarioResponseDTO> actualizarPerfil(HttpServletRequest httpRequest,
                                                               @Valid @RequestBody ActualizarPerfilRequestDTO request) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(usuarioService.actualizarPerfil(usuarioId, request));
    }

    @PutMapping("/perfil/contrasena")
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña del usuario. Requiere contraseña actual, nueva y confirmación.")
    public ResponseEntity<MensajeResponseDTO> cambiarPassword(HttpServletRequest httpRequest,
                                                             @Valid @RequestBody CambiarPasswordRequestDTO request) {
        Long usuarioId = getUsuarioId(httpRequest);
        usuarioService.cambiarPassword(usuarioId, request);
        return ResponseEntity.ok(new MensajeResponseDTO(true, "Contraseña actualizada correctamente"));
    }

    @PutMapping("/perfil/verificacion-dos-pasos")
    @Operation(summary = "Activar/Desactivar 2FA", description = "Activa o desactiva la verificación en dos pasos para el usuario")
    public ResponseEntity<UsuarioResponseDTO> actualizarVerificacionDosPasos(HttpServletRequest httpRequest,
                                                                             @RequestBody VerificacionDosPasosRequestDTO request) {
        Long usuarioId = getUsuarioId(httpRequest);
        return ResponseEntity.ok(usuarioService.actualizarVerificacionDosPasos(usuarioId, request.activo()));
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUsuarioIdFromToken(token);
    }
    @PostMapping("/sesion/actividad")
    @Operation(summary = "Registrar actividad", description = "Actualiza el último acceso del usuario para control de inactividad")
    public ResponseEntity<MensajeResponseDTO> registrarActividad(Authentication authentication) {
        usuarioService.actualizarUltimoAcceso(authentication.getName());
        return ResponseEntity.ok(new MensajeResponseDTO(true, "Actividad registrada"));
    }

}
