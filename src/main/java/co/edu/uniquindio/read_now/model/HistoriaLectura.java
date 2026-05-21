package co.edu.uniquindio.read_now.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "historias_lecturas")
public class HistoriaLectura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historia_lectura_id")
    private Long historiasLecturasId;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "recurso_id")
    private Recurso recurso;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    /** Última página vista (1-based). Null si aún no se ha registrado progreso desde el visor. */
    @Column(name = "ultima_pagina")
    private Integer ultimaPagina;

    /** Total de páginas del PDF cuando se conoce (p. ej. al cargar el documento). */
    @Column(name = "total_paginas")
    private Integer totalPaginas;

    /** Resaltados y anotaciones del visor PDF (JSON de ngx-extended-pdf-viewer, sin IDs). */
    @Column(name = "resaltados_json", columnDefinition = "TEXT")
    private String resaltadosJson;
}
