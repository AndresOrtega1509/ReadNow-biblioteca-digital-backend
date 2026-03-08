package co.edu.uniquindio.read_now.service;

import co.edu.uniquindio.read_now.dto.request.CategoriaRecursoRequestDTO;
import co.edu.uniquindio.read_now.model.CategoriaRecurso;
import co.edu.uniquindio.read_now.repository.ICategoriaRecursoRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.service.impl.CategoriaRecursoServiceImpl;
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
class CategoriaRecursoServiceImplTest {

    @Mock
    private ICategoriaRecursoRepository categoriaRecursoRepository;

    @Mock
    private IRecursoRepository recursoRepository;

    @InjectMocks
    private CategoriaRecursoServiceImpl categoriaRecursoService;

    private CategoriaRecurso categoriaExistente;

    @BeforeEach
    void setUp() {
        categoriaExistente = CategoriaRecurso.builder()
                .categoriaRecursoId(1L)
                .nombre("Novela")
                .build();
    }

    @Test
    @DisplayName("8. crear - lanza excepción cuando el nombre ya existe")
    void crear_lanzaExcepcionCuandoNombreDuplicado() {
        CategoriaRecursoRequestDTO request = new CategoriaRecursoRequestDTO("Novela");
        when(categoriaRecursoRepository.findByNombre("Novela")).thenReturn(Optional.of(categoriaExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                categoriaRecursoService.crear(request)
        );

        assertTrue(ex.getMessage().contains("Ya existe una categoría"));
        assertTrue(ex.getMessage().contains("Novela"));
        verify(categoriaRecursoRepository, never()).save(any());
    }

    @Test
    @DisplayName("9. crear - crea categoría correctamente cuando el nombre no existe")
    void crear_creaCategoriaCorrectamente() {
        CategoriaRecursoRequestDTO request = new CategoriaRecursoRequestDTO("  Poesía  ");
        CategoriaRecurso categoriaNueva = CategoriaRecurso.builder()
                .categoriaRecursoId(2L)
                .nombre("Poesía")
                .build();

        when(categoriaRecursoRepository.findByNombre("Poesía")).thenReturn(Optional.empty());
        when(categoriaRecursoRepository.save(any(CategoriaRecurso.class))).thenReturn(categoriaNueva);

        var resultado = categoriaRecursoService.crear(request);

        assertNotNull(resultado);
        assertEquals(2L, resultado.categoriaRecursoId());
        assertEquals("Poesía", resultado.nombre());
        verify(categoriaRecursoRepository).save(any(CategoriaRecurso.class));
    }
}
