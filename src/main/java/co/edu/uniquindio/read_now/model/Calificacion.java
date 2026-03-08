package co.edu.uniquindio.read_now.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "calificaciones")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "calificacion_id")
    private Long calificacionId;

    @ManyToOne
    @JoinColumn( name = "recurso_id")
    private Recurso recurso;

    @ManyToOne
    @JoinColumn ( name = "usuario_id")
    private Usuario usuario;

    private int valor;
}
