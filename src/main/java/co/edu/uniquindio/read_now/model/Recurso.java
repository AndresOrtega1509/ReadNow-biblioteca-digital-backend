package co.edu.uniquindio.read_now.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "recursos")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurso_id")
    private Long recursoId;

    private String nombre;
    private String autor;
    private String descripcion;
    private String idioma;

    @Column(name = "url_archivo")
    private String urlArchivo;

    @Column(name = "url_portada")
    private String urlPortada;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    private String activo;

    @ManyToOne
    @JoinColumn(name = "tipo_recurso_id")
    private TipoRecurso tipoRecurso;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaRecurso categoriaRecurso;
}
