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
@Table( name="suscripciones")
public class Suscripcion {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="suscripcion_id")
    private Long suscripcionId;
    private String nombre;
    private String descripcion;
    private int duracion;
    private double precio;

}
