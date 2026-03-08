package co.edu.uniquindio.read_now.config;

import co.edu.uniquindio.read_now.model.CategoriaRecurso;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Rol;
import co.edu.uniquindio.read_now.model.TipoRecurso;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.repository.ICategoriaRecursoRepository;
import co.edu.uniquindio.read_now.repository.IRecursoRepository;
import co.edu.uniquindio.read_now.repository.IRolRepository;
import co.edu.uniquindio.read_now.repository.ITipoRecursoRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IRolRepository rolRepository;
    private final IUsuarioRepository usuarioRepository;
    private final ITipoRecursoRepository tipoRecursoRepository;
    private final ICategoriaRecursoRepository categoriaRecursoRepository;
    private final IRecursoRepository recursoRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String URL_LA_MUDANZA = "https://firebasestorage.googleapis.com/v0/b/readnow-47514.firebasestorage.app/o/la-mudanza-de-los-poderes.pdf?alt=media&token=92917850-79ba-4419-a6bf-cb0720ad41ca";
    /** Portada del libro (puedes reemplazar por la URL de tu imagen en Firebase Storage) */
    private static final String URL_PORTADA_LA_MUDANZA = "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=1600&q=80";

    @Override
    @Transactional
    public void run(String... args) {
        crearRoles();
        crearAdministrador();
        configurarPruebaSuscripcion();
        crearTiposRecurso();
        crearCategorias();
        crearRecursosIniciales();
    }

    /** Prueba: usuario "prueba" (Juan Pérez) con suscripción que vence en 2 minutos. */
    private void configurarPruebaSuscripcion() {
        usuarioRepository.findByUsername("prueba").ifPresent(u -> {
            u.setFinSuscripcion(LocalDate.now());
            u.setFinSuscripcionAt(LocalDateTime.now().plusMinutes(2));
            usuarioRepository.save(u);
            log.info("Prueba: suscripción de usuario 'prueba' (Juan Pérez) configurada para vencer en 2 minutos");
        });
    }

    private void crearRoles() {
        if (rolRepository.findByNombre("ADMIN").isEmpty()) {
            rolRepository.save(Rol.builder().nombre("ADMIN").descripcion("Administrador del sistema").build());
            log.info("Rol ADMIN creado");
        }
        if (rolRepository.findByNombre("LECTOR").isEmpty()) {
            rolRepository.save(Rol.builder().nombre("LECTOR").descripcion("Lector de la biblioteca").build());
            log.info("Rol LECTOR creado");
        }
    }

    private void crearAdministrador() {
        String emailAdmin = "ortegaandresfelipe924@gmail.com";

        // Eliminar administradores duplicados con correos anteriores
        List.of("andresortega2067@gmail.com").forEach(emailAnterior -> {
            usuarioRepository.findByEmail(emailAnterior).ifPresent(usuario -> {
                usuarioRepository.delete(usuario);
                log.info("Administrador duplicado eliminado: {}", emailAnterior);
            });
        });

        if (usuarioRepository.findByEmail(emailAdmin).isEmpty()) {
            Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .apellido("ReadNow")
                    .email(emailAdmin)
                    .telefono("3029450233")
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fechaRegistro(LocalDate.now())
                    .activo("S")
                    .ultimoAcceso(LocalDateTime.now())
                    .rol(rolAdmin)
                    .build();

            usuarioRepository.save(admin);
            log.info("Usuario administrador creado: {} / admin123", emailAdmin);
        }
    }

    private void crearTiposRecurso() {
        String[] tipos = {"Libro", "Tesis", "Revista", "Artículo", "Manual"};
        for (String tipo : tipos) {
            if (tipoRecursoRepository.findByNombre(tipo).isEmpty()) {
                tipoRecursoRepository.save(TipoRecurso.builder().nombre(tipo).build());
                log.info("Tipo de recurso creado: {}", tipo);
            }
        }
    }

    private void crearCategorias() {
        String[] categorias = {"Ciencia Ficción", "Historia", "Tecnología", "Matemáticas",
                "Literatura", "Filosofía", "Medicina", "Derecho", "Ingeniería", "Arte"};
        for (String categoria : categorias) {
            if (categoriaRecursoRepository.findByNombre(categoria).isEmpty()) {
                categoriaRecursoRepository.save(CategoriaRecurso.builder().nombre(categoria).build());
                log.info("Categoría creada: {}", categoria);
            }
        }
    }

    private void crearRecursosIniciales() {
        TipoRecurso tipoLibro = tipoRecursoRepository.findByNombre("Libro")
                .orElseThrow(() -> new RuntimeException("Tipo de recurso Libro no encontrado"));
        CategoriaRecurso categoriaHistoria = categoriaRecursoRepository.findByNombre("Historia").orElse(null);

        recursoRepository.findFirstByNombreIgnoreCase("La mudanza de los poderes").ifPresentOrElse(
                recursoExistente -> {
                    if (recursoExistente.getUrlPortada() == null || recursoExistente.getUrlPortada().isBlank()) {
                        recursoExistente.setUrlPortada(URL_PORTADA_LA_MUDANZA);
                        if (categoriaHistoria != null && recursoExistente.getCategoriaRecurso() == null) {
                            recursoExistente.setCategoriaRecurso(categoriaHistoria);
                        }
                        recursoRepository.save(recursoExistente);
                        log.info("Recurso actualizado con portada: La mudanza de los poderes");
                    }
                },
                () -> {
                    Recurso recurso = Recurso.builder()
                            .nombre("La mudanza de los poderes")
                            .autor("Salvador Gallardo Cabrera")
                            .descripcion("Libro en PDF disponible en el catálogo.")
                            .idioma("español")
                            .urlArchivo(URL_LA_MUDANZA)
                            .urlPortada(URL_PORTADA_LA_MUDANZA)
                            .fechaPublicacion(LocalDate.now())
                            .activo("S")
                            .tipoRecurso(tipoLibro)
                            .categoriaRecurso(categoriaHistoria)
                            .build();
                    recursoRepository.save(recurso);
                    log.info("Recurso inicial creado: La mudanza de los poderes (enlace a Firebase Storage)");
                }
        );
        int actualizados = recursoRepository.actualizarPortadaSiVacia("mudanza", URL_PORTADA_LA_MUDANZA);
        if (actualizados > 0) {
            log.info("Portada asignada por UPDATE directo a {} recurso(s) con nombre 'mudanza'", actualizados);
        }
    }
}
