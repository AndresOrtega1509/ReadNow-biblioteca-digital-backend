package co.edu.uniquindio.read_now.dto.response;

import co.edu.uniquindio.read_now.model.enums.EstadoPqr;
import co.edu.uniquindio.read_now.model.enums.TipoPqr;

import java.time.LocalDateTime;

public record PqrAdminResumenResponseDTO(
        Long pqrId,
        TipoPqr tipo,
        String asunto,
        EstadoPqr estado,
        Long usuarioId,
        String usuarioNombre,
        String usuarioEmail,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        int totalMensajes
) {}
