package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.CalificacionRequestDTO;
import co.edu.uniquindio.read_now.dto.response.CalificacionResponseDTO;
import co.edu.uniquindio.read_now.model.Calificacion;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.ICalificacionRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.impl.CalificacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceImplTest {

    @Mock
    private ICalificacionRepository calificacionRepository;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private IRecursoRepository recursoRepository;

    @InjectMocks
    private CalificacionServiceImpl calificacionService;

    private Usuario usuario;
    private Recurso recurso;
    private Calificacion calificacionExistente;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .usuarioId(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .build();
        recurso = Recurso.builder().recursoId(1L).build();
        calificacionExistente = Calificacion.builder()
                .calificacionId(1L)
                .usuario(usuario)
                .recurso(recurso)
                .valor(3)
                .build();
    }

    @Test
    @DisplayName("6. calificar - crea nueva calificación cuando no existe")
    void calificar_creaNuevaCalificacionCuandoNoExiste() {
        CalificacionRequestDTO request = new CalificacionRequestDTO(1L, 5);
        Calificacion calificacionNueva = Calificacion.builder()
                .calificacionId(1L)
                .usuario(usuario)
                .recurso(recurso)
                .valor(5)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(calificacionRepository.findByUsuarioAndRecurso(usuario, recurso)).thenReturn(Optional.empty());
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacionNueva);

        CalificacionResponseDTO resultado = calificacionService.calificar(request, 1L);

        assertNotNull(resultado);
        assertEquals(5, resultado.valor());
        assertEquals(1L, resultado.recursoId());
        assertEquals(1L, resultado.usuarioId());
        assertTrue(resultado.nombreUsuario().contains("Juan"));
        verify(calificacionRepository).save(any(Calificacion.class));
    }

    @Test
    @DisplayName("7. calificar - actualiza calificación existente")
    void calificar_actualizaCalificacionExistente() {
        CalificacionRequestDTO request = new CalificacionRequestDTO(1L, 5);
        calificacionExistente.setValor(5);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(calificacionRepository.findByUsuarioAndRecurso(usuario, recurso))
                .thenReturn(Optional.of(calificacionExistente));
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacionExistente);

        CalificacionResponseDTO resultado = calificacionService.calificar(request, 1L);

        assertNotNull(resultado);
        assertEquals(5, resultado.valor());
        verify(calificacionRepository).save(calificacionExistente);
    }
}
