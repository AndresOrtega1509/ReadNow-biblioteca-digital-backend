package co.edu.uniquindio.read_now.dto.response;

import co.edu.uniquindio.read_now.model.enums.EstadoPqr;
import co.edu.uniquindio.read_now.model.enums.TipoPqr;

import java.time.LocalDateTime;
import java.util.List;

public record PqrDetalleResponseDTO(
        Long pqrId,
        TipoPqr tipo,
        String asunto,
        String descripcion,
        EstadoPqr estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        Long usuarioId,
        String usuarioNombre,
        String usuarioEmail,
        List<PqrMensajeResponseDTO> mensajes
) {}
