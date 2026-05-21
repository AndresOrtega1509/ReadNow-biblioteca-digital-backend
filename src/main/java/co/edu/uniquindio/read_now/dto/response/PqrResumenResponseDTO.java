package co.edu.uniquindio.read_now.dto.response;

import co.edu.uniquindio.read_now.model.enums.EstadoPqr;
import co.edu.uniquindio.read_now.model.enums.TipoPqr;

import java.time.LocalDateTime;

public record PqrResumenResponseDTO(
        Long pqrId,
        TipoPqr tipo,
        String asunto,
        EstadoPqr estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        int totalMensajes
) {}
