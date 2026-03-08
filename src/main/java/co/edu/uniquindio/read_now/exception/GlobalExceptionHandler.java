package co.edu.uniquindio.read_now.exception;

import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MensajeResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Validación: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensajeResponseDTO(false, ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MensajeResponseDTO> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Violación de integridad: {}", ex.getMessage());
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        if (msg != null && msg.contains("Duplicate")) {
            msg = "Ya existe un registro con ese valor. Por favor usa otro nombre.";
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensajeResponseDTO(false, msg != null ? msg : "Error de integridad de datos"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MensajeResponseDTO> handleRuntimeException(RuntimeException ex) {
        log.error("Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensajeResponseDTO(false, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensajeResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensajeResponseDTO(false, errores));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<MensajeResponseDTO> handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MensajeResponseDTO(false, "Recurso no encontrado. Reinicia el backend para cargar los cambios."));
    }

    @ExceptionHandler(SuscripcionVencidaException.class)
    public ResponseEntity<MensajeResponseDTO> handleSuscripcionVencida(SuscripcionVencidaException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new MensajeResponseDTO(false, ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MensajeResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new MensajeResponseDTO(false, "No tienes permisos para realizar esta acción"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeResponseDTO> handleGenericException(Exception ex) {
        log.error("Error inesperado: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensajeResponseDTO(false, "Ha ocurrido un error interno en el servidor"));
    }
}
