package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.request.ChatMensajeRequestDTO;
import co.edu.uniquindio.read_now.dto.response.ChatMensajeResponseDTO;
import co.edu.uniquindio.read_now.exception.SuscripcionVencidaException;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IChatbotService;
import co.edu.uniquindio.read_now.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Asistente", description = "Chat contextual; solo usuarios con acceso al catálogo (suscripción activa o ADMIN)")
public class ChatbotController {

    private final IChatbotService chatbotService;
    private final IUsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @PostMapping("/mensaje")
    @Operation(summary = "Enviar mensaje al asistente", description = "Requiere JWT. Solo si puede acceder al catálogo (misma regla que el contenido).")
    public ResponseEntity<ChatMensajeResponseDTO> mensaje(
            HttpServletRequest httpRequest,
            @Valid @RequestBody ChatMensajeRequestDTO request) {
        Long usuarioId = getUsuarioId(httpRequest);
        if (!usuarioService.puedeAccederAlCatalogo(usuarioId)) {
            throw new SuscripcionVencidaException();
        }
        String respuesta = chatbotService.responder(usuarioId, request);
        return ResponseEntity.ok(new ChatMensajeResponseDTO(respuesta));
    }

    private Long getUsuarioId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new RuntimeException("Token no válido");
        }
        return jwtUtil.getUsuarioIdFromToken(auth.substring(7));
    }
}
