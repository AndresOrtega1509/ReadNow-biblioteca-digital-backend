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
@Table(name = "resenias")
public class Resenia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resenia_id")
    private Long reseniaId;

    @ManyToOne
    @JoinColumn(name = "recurso_id")
    private Recurso recurso;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
