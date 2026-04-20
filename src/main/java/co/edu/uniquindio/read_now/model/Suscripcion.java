package co.edu.uniquindio.read_now.model;

import jakarta.persistence.*;
import lombok.*;

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

    /** MENSUAL, SEMESTRAL, ANUAL — identificador estable para checkout y webhooks. */
    @Column(name = "codigo_plan", unique = true, length = 32)
    private String codigoPlan;

    /** ID de Price en Stripe (price_...); vacío hasta configurar en Dashboard. */
    @Column(name = "stripe_price_id", length = 128)
    private String stripePriceId;

}
