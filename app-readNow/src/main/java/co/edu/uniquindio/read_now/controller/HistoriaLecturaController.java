package co.edu.uniquindio.read_now.controller;

import co.edu.uniquindio.read_now.dto.response.HistoriaLecturaResponseDTO;
import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import co.edu.uniquindio.read_now.exception.SuscripcionVencidaException;
import co.edu.uniquindio.read_now.security.JwtUtil;
import co.edu.uniquindio.read_now.service.IHistoriaLecturaService;
import co.edu.uniquindio.read_now.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
@Tag(name = "Historial de Lectura", description = "Endpoints para gestionar el historial de lecturas del usuario")
public class HistoriaLecturaController {

    //private final IHistoriaLecturaService historiaLecturaService;
    private final IUsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    private Long getUsuarioId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.getUsuarioIdFromToken(token);
    }
}
