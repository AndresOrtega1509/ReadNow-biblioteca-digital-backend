package co.edu.uniquindio.read_now.dto.response;

import java.time.LocalDateTime;

public record HistoriaLecturaResponseDTO(
        Long historiaLecturaId,
        Long recursoId,
        String nombreRecurso,
        String autorRecurso,
        LocalDateTime fechaLectura
) {}
