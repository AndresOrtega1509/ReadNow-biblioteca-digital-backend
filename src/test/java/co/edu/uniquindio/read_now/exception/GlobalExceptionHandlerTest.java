package co.edu.uniquindio.read_now.exception;

import co.edu.uniquindio.read_now.dto.response.MensajeResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("GlobalExceptionHandler - handleIllegalArgument devuelve 400 con mensaje")
    void handleIllegalArgument_devuelve400ConMensaje() {
        String mensaje = "El nombre de usuario ya está en uso";
        IllegalArgumentException ex = new IllegalArgumentException(mensaje);

        ResponseEntity<MensajeResponseDTO> response = handler.handleIllegalArgument(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().exitoso());
        assertEquals(mensaje, response.getBody().mensaje());
    }
}
